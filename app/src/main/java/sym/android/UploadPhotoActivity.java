package sym.android;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedInputStream;
//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Bhavya on 4/16/2016.
 */
public class UploadPhotoActivity extends Activity implements View.OnClickListener {

    private Button btnUploadPhots;
    Uri fileUri;
    ArrayList<String> imagesPathList;
    String filePath;
    DatabaseHandler db;
    private LinearLayout lnrImages;
    private Bitmap yourbitmap;
    List<MetaData> metaDataList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        System.out.println("Inside Upload ");
        db = new DatabaseHandler(this);
        lnrImages = (LinearLayout) findViewById(R.id.lnrImages1);
        imagesPathList = new ArrayList<String>();
        long id=0;

        Intent intent = getIntent();
        String[] imagesPath = intent.getStringExtra("data").split("\\|");
        //System.out.println("Images Path from intent "+imagesPath[0]+" Length "+imagesPath.length);
        metaDataList = new ArrayList<MetaData>() ;

        //setting the created date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date());

        try{
            lnrImages.removeAllViews();
        }catch (Throwable e){
            e.printStackTrace();
        }
        for (int i=0;i<imagesPath.length;i++){
            imagesPathList.add(imagesPath[i]);
            id = db.getMaxId()+1;
            metaDataList.add(new MetaData(id, date,"dummy", "dummy", "dummy" ));
            yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);

            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(yourbitmap);
            imageView.setAdjustViewBounds(true);
            lnrImages.addView(imageView);
        }

        btnUploadPhots = (Button)findViewById(R.id.btnUploadPhots);
        btnUploadPhots.setOnClickListener(this);


    }

        @Override
    public void onClick(View view) {

             showChangeLangDialog();

            addMetaData(metaDataList);

            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();

    }

    public void addMetaData(List<MetaData> metaDataList){


        for(MetaData md : metaDataList){
            db.addMetaData(md);
        }

        List<MetaData> metadataReturn = db.getAllMetaData();
        for (MetaData md : metadataReturn) {
            String log = "Id: "+md.getId()+" ,Created Date: " + md.getCreatedDate() + " ,UserList: "
                    + md.getUserList()+" ,fileName: "+md.getFileName() + " ,groupName:"+md.getGroupName();
            // Writing Contacts to log
            System.out.println("Data: "+ log);
            System.out.println("Max Id"+db.getMaxId());
        }
        db.close();
        //Call method to add meta data to DB
    }

    public void showChangeLangDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        final String result;
        dialogBuilder.setView(dialogView);

        final EditText userInput = (EditText) dialogView.findViewById(R.id.edit1);

        dialogBuilder.setTitle("Group Name");
        dialogBuilder.setMessage("Enter Group Name");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //do something with edt.getText().toString();

                 userInput.getText().toString();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
                dialog.cancel();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();


    }



}
