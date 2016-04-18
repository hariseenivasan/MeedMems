package sym.android;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import sym.android.R;


public class ContactsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        String[] DUMMY_CREDENTIALS = {"Andy" , "Brian", "Cindy" ,"Dan"};
        ListAdapter ContactAdapter = new CustomAdapter(this, DUMMY_CREDENTIALS);
        ListView ContactsListView = (ListView) findViewById(R.id.ContactsListView);
        ContactsListView.setAdapter(ContactAdapter);

        ContactsListView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener(){
                        @Override
                        public void onItemClick(AdapterView<?> parent,View view, int position, long id){
                            String dummy_credential = String.valueOf(parent.getItemAtPosition(position));
                            Toast.makeText(ContactsActivity.this,dummy_credential,Toast.LENGTH_LONG).show();
                        }
                    }
        );

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


    }

}
