package sym.android;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jGit.sym.src.GeneralUtil;
import jGit.sym.src.JGitOps;
import jGit.sym.src.TestJGit;
import sym.android.R;

public class GroupsActivity extends AppCompatActivity {
    private final int PICK_IMAGE_MULTIPLE = 100;
    private final int CONTACTS_RETURN = 200;
    String groupName;
    ListView listViewGroup;
    ArrayAdapter<String> groupNameArray;
    ArrayAdapter<String> numContactsArray;
    ArrayAdapter<String> createdDataArray;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        Intent i = getIntent();
        StrictMode.setThreadPolicy(policy);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAddPhoto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getGroupNameAndAlbums();
                Log.d("Alert", "Show alert");


            }
        });
        TextView tv = (TextView) findViewById(R.id.WaterMark);
         listViewGroup = (ListView) findViewById(R.id.LayoutList);

        //Populate the groups from metadata.
        DatabaseHandler db = new DatabaseHandler(this);
        try {
           List<MetaData> groups= db.getAllMetaData();
            if(groups == null) {
                listViewGroup.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.VISIBLE);
            }
            else if(groups.size()==0) {
                listViewGroup.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.VISIBLE);
            }else{
                List<MetaData> mDataList = db.getAllMetaData();
                listViewGroup.setVisibility(View.VISIBLE);
                tv.setVisibility(View.INVISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* try {
            JGitOps jg = new JGitOps(getApplicationContext());

        jg.listRemoteRepos();
            System.out.print("Clone done");

        } catch (Exception e) {
            e.printStackTrace();
        }*/


        Intent nActivity = new  Intent(this,NotificationActivity.class);
        //startActivity(nActivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    public ArrayList<Uri> processAlbumDataForURI(Intent data){
        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
        if(data.getData()!=null){
            //One Image Selected
            Uri mImageUri=data.getData();
            mArrayUri.add(mImageUri);
        }else {
            //Multiple Image
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                for (int i = 0; i < mClipData.getItemCount(); i++) {
                    Log.d("ClipData index","Index: "+i+" Count "+mClipData.getItemCount());
                    ClipData.Item item = mClipData.getItemAt(i);
                    Uri uri = item.getUri();
                    if(uri!=null)
                        mArrayUri.add(uri);
                }
                Log.v("LOG_TAG", "Selected Images " + mArrayUri.size());
            }
        }
        return mArrayUri;
    }

    public void CreateandSendMetadata(String groupName,ArrayList<Uri> uris,ArrayList<String> emailId) throws JSONException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());
        Log.d("CreateAndSendMetaData","URI size: "+uris.size());
        MetaData mData = PathMethods.processURIArray(this, uris,groupName);
        mData.setCreatedDate(date);
        mData.setUserList(emailId);

        Map<String,String> map = ((TokenFetcher) this.getApplication()).getTokenMap();
        for(String eachEmail:emailId) {
            String token = map.get(eachEmail);
            Log.d("SendMessage","Token: for Email: "+eachEmail+" is: "+token);

            String dataJson = "{ \""+MyGcmListenerService.GCM_JSON_DATA+"\": {\n" +
                    "    \""+MyGcmListenerService.GCM_JSON_KEY_GREETING+"\": \""+getString(R.string.notification_sync_greeting)+"\",\n" +
                    "    \""+MyGcmListenerService.GCM_JSON_KEY_OPERATION+"\": \"" + DatabaseHandler.DB_OP_ADD + "\",\n" +
                    "    \""+MyGcmListenerService.GCM_JSON_KEY_SENTBY+"\": \"" + LoginActivity.acct.getEmail() + "\",\n" +
                    "    \""+DatabaseHandler.KEY_GROUPNAME+"\": \"" + mData.getGroupName() + "\",\n" +
                    "    \""+DatabaseHandler.KEY_CREATEDDATE+"\": \"" + mData.getCreatedDate() + "\",\n" +
                    "    \""+DatabaseHandler.KEY_SIZE+"\": " + new JSONArray(mData.getSizeList()) + ",\n" +
                    "    \""+DatabaseHandler.KEY_FILENAME+"\": " + new JSONArray(mData.getFileNameList()) + ",\n" +
                    "    \""+DatabaseHandler.KEY_USERLIST+"\": " + new JSONArray(mData.getUserList()) + ",\n" +
                    "    \""+MyGcmListenerService.GCM_JSON_KEY_MESSAGE+"\": \"" + mData.getGroupName()  + "\",\n" +
                    "  },\n" +
                    "  \""+MyGcmListenerService.GCM_JSON_TO+"\" : \"" + token + "\"\n" +
                    "}";

            try {
                DatabaseHandler db = new DatabaseHandler(this);
                db.addMetaData(mData);
                GeneralUtil.sendPost(dataJson);
                Log.e("Message", "Message Body "+ dataJson);
                Log.e("Message", "Message Sent to "+ eachEmail);
            } catch (Exception e) {
                Log.e("Message", "Message not send to "+ eachEmail);
            }
        }
         //   if (GeneralUtil.pushPhotos(mData))
          //      Log.d("Push Photos", "Success");
          //  else
           //     Log.e("Push Photos", "Error ");

    }
    ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
                //Get the Album URI
                mArrayUri = processAlbumDataForURI(data);


                Intent uploadIntent = new Intent(this, ContactsActivity.class);
                startActivityForResult(uploadIntent, CONTACTS_RETURN);


            } else if(requestCode == CONTACTS_RETURN && resultCode == ContactsActivity.RESULT_OK) {
                ArrayList<String> emailId = data.getStringArrayListExtra("data");
                CreateandSendMetadata(groupName,mArrayUri,emailId);
                Toast.makeText(this, "Sending Photos to contacts",
                        Toast.LENGTH_LONG).show();

            }else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
        //start upload activity
       /* Intent uploadIntent = new Intent(this, UploadPhotoActivity.class);
        uploadIntent.putParcelableArrayListExtra("uris", mArrayUri);
        startActivity(uploadIntent);
        super.onActivityResult(requestCode, resultCode, data);
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_groups, menu);
        return true;// super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.logout:

                Intent i =new Intent(getApplicationContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("Logout",true);
                startActivity(i);
                return true;
            case R.id.action_settings:
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void getGroupNameAndAlbums(){
        AlertDialog.Builder alert = new AlertDialog.Builder(GroupsActivity.this);
        final EditText edittext = new EditText(GroupsActivity.this);
        edittext.setGravity(Gravity.CENTER_HORIZONTAL);
        alert.setMessage("Whats Group Name?")
                .setTitle("Album Group Name ")
                .setView(edittext)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        groupName = edittext.getText().toString();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);


                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                        groupName = null;
                    }
                })
                .create().show();
    }
}
