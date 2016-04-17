package com.example.bhavya.myapplication_synced;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CheckedInputStream;
//import android.support.v7.app.AppCompatActivity;

/**
 * Created by Aishwarya on 4/16/2016.
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

        try{
            lnrImages.removeAllViews();
        }catch (Throwable e){
            e.printStackTrace();
        }
        for (int i=0;i<imagesPath.length;i++){
            imagesPathList.add(imagesPath[i]);
            id = db.getMaxId()+1;
            metaDataList.add(new MetaData(id, 0,"dummy", "dummy" ));
            yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);
            //ImageView imageView = (ImageView) findViewById(R.id.imageViewUpload);
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
            addMetaData(metaDataList);

            Intent i = new Intent();
            setResult(Activity.RESULT_OK, i);
            finish();

    }

    public void addMetaData(List<MetaData> metaDataList){
        Long id = db.getMaxId()+1;
        //MetaData metaData = new MetaData(id, 1234567809, "abc,xyz", "me");

        for(MetaData md : metaDataList){
            db.addMetaData(md);
        }

        List<MetaData> metadataReturn = db.getAllMetaData();
        for (MetaData md : metadataReturn) {
            String log = "Id: "+md.getId()+" ,CRC: " + md.getImageCRC() + " ,UserList: " + md.getUserList()+" ,Owner: "+md.getOwner();
            // Writing Contacts to log
            System.out.println("Data: "+ log);
            System.out.println("Max Id"+db.getMaxId());
        }
        db.close();
        //Call method to add meta data to DB
    }






}
