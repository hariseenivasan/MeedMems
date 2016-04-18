package sym.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.IOException;

import jGit.sym.src.JGitOps;
import jGit.sym.src.TestJGit;
import sym.android.R;

public class GroupsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        Intent i = getIntent();
        GoogleSignInAccount acc = (GoogleSignInAccount)i.getParcelableExtra("GoogleAccount");
        Log.d("Google Account Email", acc.getEmail());
        StrictMode.setThreadPolicy(policy);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        try {
            JGitOps jg = new JGitOps(getApplicationContext());

        jg.listRemoteRepos();
            System.out.print("Clone done");

        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent nActivity = new  Intent(this,NotificationActivity.class);
        startActivity(nActivity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 1. Create a git repository
     * 2. Send a notification to the other phone
     */
}
