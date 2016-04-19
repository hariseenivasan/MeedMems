package sym.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.lang.ArrayUtils;

import java.util.ArrayList;
import java.util.Map;

import sym.android.R;


public class ContactsActivity extends AppCompatActivity {
    AutoCompleteTextView textView;
    Map<String,String> mapTest;
    CustomAdapter ContactAdapter;
    String[] List_Adapter_values;
    ListView ContactsListView;
    private long selectedid;
    private boolean isSelected;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        String[] DUMMY_CREDENTIALS = {"Hari Seenivasan" , "Kiran Nadigala", "Tejaswini" ,"Shamrin","Bhavya"};

        ContactsListView = (ListView) findViewById(R.id.ContactsListView);

        mapTest = ((TokenFetcher) this.getApplication()).getTokenMap();
        String[] searchable;
        if(mapTest!=null) {

           searchable = (String[]) ArrayUtils.addAll(mapTest.keySet().toArray(new String[mapTest.keySet().size()]),DUMMY_CREDENTIALS);
        }
        else {
            searchable = DUMMY_CREDENTIALS;
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, searchable);
         textView = (AutoCompleteTextView)
                findViewById(R.id.ContactsSearch);
        textView.setAdapter(adapter);

        ContactsListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String dummy_credential = String.valueOf(parent.getItemAtPosition(position));
                        Toast.makeText(ContactsActivity.this, dummy_credential, Toast.LENGTH_LONG).show();
                    }
                }
        );
        textView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isSelected = true;
                selectedid = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                isSelected = false;
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Button btnAddContact = (Button)findViewById(R.id.addContact);
        btnAddContact.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                    String txt = (String) textView.getText().toString();
                    Log.d("BtnClick", "txt: " + txt);
                    //if(mapTest.containsKey(txt)){
                    textView.setText("");
                    ArrayAdapter<String> a =( ArrayAdapter<String>) textView.getAdapter();
                    a.remove(txt);
                    textView.setAdapter(a);
                            List_Adapter_values = (String[]) ArrayUtils.addAll(List_Adapter_values, new String[]{txt});
                    Log.d("BtnClick", List_Adapter_values[0]);
                    ContactAdapter = new CustomAdapter(getApplicationContext(), List_Adapter_values);
                    ContactsListView.setAdapter(ContactAdapter);
                    //}

            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_contacts, menu);
        return true;// super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_done:

                Intent i =new Intent(getApplicationContext(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("data",List_Adapter_values);
               setResult(200);
                finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
