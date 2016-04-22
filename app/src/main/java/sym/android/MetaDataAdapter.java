package sym.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
        Log.d("GetView", "Position "+position);
        Log.d("GetView", "GetItem "+getItem(position));
        Log.d("GetView", "Position "+getItem(position).getGroupName());
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

       // contactImage.setImageResource(R.mipmap.images);
     /*   Bitmap bitmap = MediaStore.Images.Thumbnails.getThumbnail(
                getContext().getContentResolver(), Uri.fromFile(new File(listImageUri.get(0))).getQuery(),
                MediaStore.Images.Thumbnails.MINI_KIND,
                (BitmapFactory.Options) null );
*/
        return customView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
