package sym.android;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jGit.sym.src.JGitOps;
import jGit.sym.src.TestJGit;
import sym.android.R;

public class GroupsActivity extends AppCompatActivity {
    private final int PICK_IMAGE_MULTIPLE = 100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        Intent i = getIntent();
        GoogleSignInAccount acc = i.getParcelableExtra("GoogleAccount");
        Log.d("Google Account Email", acc.getEmail());
        StrictMode.setThreadPolicy(policy);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.btnAddPhoto);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);

            }
        });
        TextView tv = (TextView) findViewById(R.id.WaterMark);
        View includedLayout = findViewById(R.id.LayoutList);

        //Populate the groups from metadata.
        DatabaseHandler db = new DatabaseHandler(this);
        try {
           List<MetaData> groups= db.getAllMetaData();
            if(groups == null) {
                includedLayout.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.VISIBLE);
            }
            else if(groups.size()==0) {
                includedLayout.setVisibility(View.INVISIBLE);
                tv.setVisibility(View.VISIBLE);
            }else{

                includedLayout.setVisibility(View.VISIBLE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();

        try {
            // When an Image is picked
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
                    && null != data) {
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
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }
        //start upload activity
        Intent uploadIntent = new Intent(this, UploadPhotoActivity.class);
        uploadIntent.putParcelableArrayListExtra("uris", mArrayUri);
        startActivity(uploadIntent);
        super.onActivityResult(requestCode, resultCode, data);

    }

    /**
     * 1. Create a git repository
     * 2. Send a notification to the other phone
     */
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
}
