package com.bitspilani.admin.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bitspilani.admin.R;
import com.bitspilani.admin.util.AppConstants;
import com.bitspilani.admin.util.Faculty;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener, ValueEventListener {
    private static final String TAG = "LoginActivity";
    CallbackManager callbackManager;
    GoogleApiClient googleApiClient;
    GoogleSignInOptions googleSignInOptions;
    FacebookCallback<LoginResult> facebookCallback;
    SharedPreferences userData;
    ProgressDialog progressDialog;
    Faculty faculty;
    FirebaseDatabase firebaseDB;
    DatabaseReference dbReference;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppConstants.GOOGLE_SIGNIN) {        //If logging in from Google
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleCallback(result);
        } else {        //Logging in from Facebook
            if (callbackManager != null) {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void googleCallback(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                String email = acct.getEmail();
                String firstName = acct.getDisplayName();
                faculty = new Faculty();
                faculty.setEmail(email);
                faculty.setFirstName(firstName);
                doLogin(faculty);
            } else {
                Toast.makeText(getCurrentActivity(), "Cannot retrieve email from Google", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getCurrentActivity(), "SignIn to Google Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void doLogin(Faculty faculty) {
        this.faculty = faculty;
        this.firebaseDB = FirebaseDatabase.getInstance();
        this.dbReference = firebaseDB.getReference();
        dbReference.child("auth_ids").addListenerForSingleValueEvent(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
        userData = getSharedPreferences(AppConstants.USER_DATA, MODE_APPEND);
        String id;
        if(!(id = userData.getString(AppConstants.ID, "")).isEmpty()) {
            progressDialog.setMessage("Please wait; We are logging you in.");
            identifyUser(id);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        this.setContentView(R.layout.activity_login);
        initializeSocialObjects();
        //Check if already logged in using FB and logout if already logged in and email not present
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        callbackManager = CallbackManager.Factory.create();

        Button btnFbLogin = (Button) findViewById(R.id.btn_facebook_login);
        Button btnGoogleLogin = (Button) findViewById(R.id.btn_google_login);
        if (btnFbLogin != null) {
            btnFbLogin.setOnClickListener(this);
        }
        if (btnGoogleLogin != null) {
            btnGoogleLogin.setOnClickListener(this);
        }
    }

    private void identifyUser(String id) {
        if(!progressDialog.isShowing()) {
            progressDialog.show();
        }
        dbReference.child("auth_ids").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(progressDialog.isShowing()) {
                    progressDialog.cancel();
                }
                Object userType =  dataSnapshot.child("userType").getValue();
                if(userType != null && userType.toString().equals("admin")) {
                    callAdminActivity();
                } else if (userType != null && userType.toString().equals("faculty")) {
                    callHomeActivity();
                } else {
                    Toast.makeText(getCurrentActivity(), "We couldn't identify you. Please login again.", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initVariables() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        this.firebaseDB = FirebaseDatabase.getInstance();
        this.dbReference = firebaseDB.getReference();
    }

    //Get the email from facebook and gotoHomeActivity if success
    private void getEmailFromFacebook(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String email = object.getString(AppConstants.EMAIL);
                    object.getString(AppConstants.FIRST_NAME);
                    faculty = new Faculty();
                    faculty.setEmail(email);
                    doLogin(faculty);
                } catch (JSONException e) {
                    throw new FacebookException("Cannot retrieve email.");
                }
            }
        });

        Bundle params = new Bundle();
        params.putString("fields", AppConstants.EMAIL);
        request.setParameters(params);
        request.executeAsync();
    }

    private Activity getCurrentActivity() {
        return this;
    }

    private void callHomeActivity() {
        Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(homeActivity);
        finish();
    }

    private void callAdminActivity() {
        Intent adminActivity = new Intent(LoginActivity.this, AdminActivity.class);
        startActivity(adminActivity);
        finish();
    }

    private void initializeSocialObjects() {
        //Facebook Callback
        facebookCallback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (loginResult.getRecentlyGrantedPermissions().contains(AppConstants.EMAIL)) {
                    getEmailFromFacebook(loginResult.getAccessToken());
                } else {
                    Toast.makeText(getApplicationContext(), "Login Unsuccessful, Cannot get Email ID", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                }
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), "Login Cancelled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
            }
        };

        //Google SignIn Objects
        googleSignInOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(ConnectionResult connectionResult) {
                Toast.makeText(getCurrentActivity(), "Cannot connect to Google", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions).build();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_facebook_login:
                progressDialog.show();
                facebookLogin();
                break;
            case R.id.btn_google_login:
                progressDialog.show();
                googleLogin();
                break;
        }
    }

    private void googleLogin() {
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(googleSignInIntent, AppConstants.GOOGLE_SIGNIN);
    }

    private void facebookLogin() {
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(AppConstants.EMAIL);
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(getCurrentActivity(), permissions);
        loginManager.registerCallback(callbackManager, facebookCallback);
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        boolean emailFound = false;
        for( DataSnapshot snapshot: dataSnapshot.getChildren() ) {
            String email = snapshot.child("email").getValue().toString();
            Log.d(TAG, "onDataChange: " +email);
            String id = snapshot.getKey();
            String userType = snapshot.child("userType").getValue().toString();
            if(email.equals(this.faculty.getEmail())) {
                emailFound = true;
                SharedPreferences.Editor editor = userData.edit();
                editor.putString(AppConstants.ID, id);
                editor.apply();
                progressDialog.cancel();

                if(userType.equals("admin")) {
                    callAdminActivity();
                } else if (userType.equals("faculty")) {
                    this.dbReference.child("faculty").child(id).updateChildren(this.faculty.getMap());
                    callHomeActivity();
                } else {
                    Toast.makeText(getCurrentActivity(), "We couldn't identify you. Please login again.", Toast.LENGTH_SHORT).show();
                }

            }
        }
        if(!emailFound) {
            progressDialog.cancel();
            Toast.makeText(getCurrentActivity(), "You are not authorised to login. Contact Admin.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}

