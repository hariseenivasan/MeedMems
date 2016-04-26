package sym.android;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by mandar on 4/18/16.
 */
public class MetaDataAdapter extends  ArrayAdapter<MetaData> {

    public MetaDataAdapter(Context context, ArrayList<MetaData> mData) {
        super(context,R.layout.content_groups, mData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater contactsInflater = LayoutInflater.from(getContext());
        View customView = contactsInflater.inflate(R.layout.content_groups, parent, false);
        /*
        Log.d("GetView", "Position "+position);
        Log.d("GetView", "GetItem "+getItem(position));
        Log.d("GetView", "Group Name: "+getItem(position).getGroupName());
        */
        TextView groupNameTxt = (TextView) customView.findViewById(R.id.group_name);
        TextView groupCreated = (TextView) customView.findViewById(R.id.group_created);
        TextView groupContactCount = (TextView) customView.findViewById(R.id.group_contact_count);
        TextView groupImageCount = (TextView) customView.findViewById(R.id.group_image_count);
        ImageView groupImage = (ImageView) customView.findViewById(R.id.group_pic);

        String groupName = getItem(position).getGroupName();
        groupNameTxt.setText(groupName);
        groupCreated.setText(getItem(position).getCreatedDate());
        groupContactCount.setText(""+getItem(position).getUserList().size());
        ArrayList<String> listImageUri = getItem(position).getFileNameList();
        groupImageCount.setText(""+listImageUri.size());
       // contactImage.setImageResource(R.mipmap.images);
        try {
            //Bitmap bitmap = getThumbnail(getContext().getContentResolver(),listImageUri.get(0));
            //groupImage.setBackgroundColor(Color.RED);
            //Log.d("BITMAP","BITMAP Set "+bitmap.getByteCount());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("BITMAP", "BITMAP ERROR "+e.getMessage());
        }

        return customView;
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }

    public static Bitmap getThumbnail(ContentResolver cr, String path) throws Exception {

        //Cursor ca = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[] { MediaStore.MediaColumns._ID }, MediaStore.MediaColumns.DATA + "=?", new String[] {path}, null);
        String[] projection = new String[] {MediaStore.Images.Thumbnails._ID, MediaStore.Images.Thumbnails.IMAGE_ID};
        Cursor ca = cr.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);

        if (ca != null && ca.moveToFirst()) {
            int id = ca.getInt(ca.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID));
            Log.d("ID","cursor ID "+id);
            ca.close();
            return MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MICRO_KIND, null );
        }

        ca.close();
        Log.e("ID", "cursor NULL ");
        return null;

    }
}
