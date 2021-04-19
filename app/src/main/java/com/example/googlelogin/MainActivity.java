package com.example.googlelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CallbackManager callbackManager;
    LoginButton loginButton;
    Userdata ud = new Userdata();
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
            SignInButton signInButton = findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            findViewById(R.id.sign_in_button).setOnClickListener((View.OnClickListener) this);


            callbackManager = CallbackManager.Factory.create();
            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            loginButton.setReadPermissions(Arrays.asList("email", "user_birthday"));
            // If you are using in a fragment, call loginButton.setFragment(this);

            // Callback registration
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {

                    Log.d("Demo", "LoginSuccess");


                }

                @Override
                public void onCancel() {
                    Log.d("Demo", "LoginCancel");// App code
                }

                @Override
                public void onError(FacebookException exception) {
                    Log.d("Demo", "LoginError");
                }
            });


    }



    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null)
            {
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
        }
    };};

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.stopTracking();
    }

    @Override
    public void onClick(View v) {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(!isLoggedIn)
                signIn();
        else{
            Toast.makeText(getApplicationContext(),"Logout from facebook",Toast.LENGTH_SHORT).show();
            return;
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            GraphRequest graphRequest = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    if (object == null)
                        return;
                    else
                        try {
                            JSONObject obj = new JSONObject(object.getString("picture"));
                            JSONObject ob =new  JSONObject(obj.getString("data"));
                            ud.personPhoto = Uri.parse(ob.getString("url"));
                            ud.id = object.getString("id");
                            ud.name = object.getString("name");
                            ud.email = object.getString("email");
                            ud.loggedby = "facebook";
                            Intent startin  = new Intent(getApplicationContext(),LoggedIn.class);
                            startin.putExtra("userdata", ud);
                            startActivity(startin);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                }
            });
            Bundle bundle = new Bundle();
            bundle.putString("fields", "name,picture,id,email");
            graphRequest.setParameters(bundle);
            graphRequest.executeAsync();
        }
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                ud.email= acct.getEmail();
                ud.name= acct.getDisplayName();
                ud.id = acct.getId();
                ud.personPhoto = acct.getPhotoUrl();
                ud.loggedby = "google";
            }

            Intent startintg = new Intent(getApplicationContext(),LoggedIn.class);
            startintg.putExtra("userdata", ud);
            startActivity(startintg);

            // Signed in successfully, show authenticated UI.

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast.makeText(getApplicationContext(),"Login Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

}