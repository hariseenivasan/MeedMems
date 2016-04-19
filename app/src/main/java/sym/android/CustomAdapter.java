package sym.android;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by mandar on 4/18/16.
 */
public class CustomAdapter extends  ArrayAdapter<String> {

    public CustomAdapter(Context context, String[] DUMMY_CREDENTIALS) {
        super(context,R.layout.custom_row, DUMMY_CREDENTIALS);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater contactsInflater = LayoutInflater.from(getContext());
        View customView = contactsInflater.inflate(R.layout.custom_row, parent, false);

        String singleContact = getItem(position);
        TextView contactsText = (TextView) customView.findViewById(R.id.contactText);
        ImageView contactImage = (ImageView) customView.findViewById(R.id.contactImage);

        contactsText.setText(singleContact);
       // contactImage.setImageResource(R.mipmap.images);

        return customView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}
