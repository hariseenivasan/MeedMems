package sym.android;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import jGit.sym.src.GeneralUtil;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;
    public static  GoogleSignInAccount acct;
    // UI references.
    public static GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean logout = getIntent().getBooleanExtra("Logout", false);

        //added by kiran for a test
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_login);
        if(logout){
        
        }
       // Configure sign-in to request the user's ID, email address, and basic// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        System.out.println("GSO Sign in failed");
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)

                .build();


// Customize sign-in button. The sign-in button can be displayed in
// multiple sizes and color schemes. It can also be contextually
// rendered based on the requested scopes. For example. a red button may
// be displayed when Google+ scopes are requested, but a white button
// may be displayed when only basic profile is requested. Try adding the
// Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
// difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

      findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              switch (v.getId()) {
                  case R.id.sign_in_button:
                      signIn();
                      break;
                  // ...
              }
          }
      });
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("SilentLogin", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1234);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        if (requestCode == 1234) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }
    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("GSO", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
             acct = result.getSignInAccount();
            Intent gActivity = new  Intent(this, GroupsActivity.class);
            //Parcel a;
            //acct.writeToParcel(a);
            gActivity.putExtra("GoogleAccount", acct);

            Log.d("test_log", "*******this is test log Kiran*******");
            Log.d("the_app_path", getApplicationContext().getFilesDir().getPath());


            GeneralUtil.remoteUpdateTokenMap(getApplicationContext(), "knadigatla@gmail.com", "efmgSA7UmlE:APA91bEaFNbzaX7MBDsq0P68gPmFyYjQLJLwvzzyuLPmDmnD5qY-kRCQ3Agh1mZDTHN5wFM1_vd7uBU5XZjWo2U1GUcmNIDEHqBULsuJb9s7AujjZLQH3Y5GKx4diEoU2AkPexX4R6Pz");

                    ((TokenFetcher) this.getApplication()).setTokenMap(GeneralUtil.localUpdateTokenMap(getApplicationContext()));

            Map<String,String> mapTest = ((TokenFetcher) this.getApplication()).getTokenMap();
            Log.d("mapTest_keys", Arrays.toString(mapTest.keySet().toArray()));

            startActivity(gActivity);
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_groups, menu);
        return true;// super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item!=null) {
            switch (item.getItemId()) {
                case R.id.logout:

                    //LoginActivity.mGoogleApiClient.disconnect();

                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    return true;
                case R.id.action_settings:
                    return true;
                default:

            }
        }
        return super.onOptionsItemSelected(item);
    }

}

