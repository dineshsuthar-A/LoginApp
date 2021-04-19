package com.example.googlelogin;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;



public class LoggedIn extends AppCompatActivity {

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();
    Userdata ud = new Userdata();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);
        ImageView img = (ImageView) findViewById(R.id.imageview);
        TextView name = (TextView) findViewById(R.id.name);
        TextView emailid = (TextView) findViewById(R.id.emailid);
        TextView pid = (TextView) findViewById(R.id.personid);
        Intent i = getIntent();
        Button logout = (Button) findViewById(R.id.signout);
        ud = i.getParcelableExtra("userdata");
        if((ud.loggedby).equals("google")) {

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                name.setText(personName);
                String personEmail = acct.getEmail();
                emailid.setText(personEmail);
                String personId = acct.getId();
                pid.setText(personId);
                Uri personPhoto = acct.getPhotoUrl();
                Glide.with(this).load(personPhoto).placeholder(R.drawable.noimagefound).into(img);

            }




       }
       else
       {
           name.setText(ud.name);
           pid.setText(ud.id);
           emailid.setText(ud.email);
           Glide.with(this).load(ud.personPhoto).placeholder(R.drawable.noimagefound).into(img);
       }


       logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(ud.loggedby.equals("facebook")) {
                   ud.personPhoto = null;
                   ud.email = null;
                   ud.loggedby = null;
                   ud.name = null;
                   ud.id = null;
                   AccessToken accessToken = AccessToken.getCurrentAccessToken();
                   accessToken.setCurrentAccessToken(null);
                   Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                   Intent starthm = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(starthm);
               }
               else
               {
                   signOut();
               }

           }
       });


    }
    private void signOut(){
        ud.loggedby = "";
        GoogleSignInClient mGoogleSignInClient =GoogleSignIn.getClient(this, gso);;
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        ud.personPhoto = null;
                        ud.email = null;
                        ud.id = null;
                        ud.name = null;
                        ud.loggedby = "";
                        Toast.makeText(getApplicationContext(),"Logged Out",Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}