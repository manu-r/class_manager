package com.bitspilani.classmanager.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bitspilani.classmanager.R;
import com.bitspilani.classmanager.util.AppConstants;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    CallbackManager callbackManager;
    GoogleApiClient googleApiClient;
    GoogleSignInOptions googleSignInOptions;
    FacebookCallback<LoginResult> facebookCallback;
    SharedPreferences userData;



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
            String email;
            if (acct != null) {
                email = acct.getEmail();
                saveEmail(email);
                callHomeActivity(email);
            } else {
                Toast.makeText(getCurrentActivity(), "Cannot retrieve email from Google", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getCurrentActivity(), "SignIn to Google Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveEmail(String email) {
        userData.edit().putString(AppConstants.EMAIL, email).apply();
        Log.d(TAG, "Saved email:" +email);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());


        String email = getSharedPreferences(AppConstants.USER_DATA, Context.MODE_PRIVATE).getString(AppConstants.EMAIL, "");
        //Check already logged in
        Log.d(TAG, "Email: " +email);
        if (!email.isEmpty()) {
            callHomeActivity(email);
        }

        this.setContentView(R.layout.activity_login);
        initializeSocialObjects();
        //Check if already logged in using FB and logout if already logged in and email not present
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        userData = getSharedPreferences(AppConstants.USER_DATA, MODE_APPEND);
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

    //Get the email from facebook and gotoHomeActivity if success
    private void getEmailFromFacebook(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String email = object.getString(AppConstants.EMAIL);
                    saveEmail(email);
                    callHomeActivity(email);
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

    private void callHomeActivity(String email) {
        Intent homeActivity = new Intent(LoginActivity.this, HomeActivity.class);
        homeActivity.putExtra(AppConstants.EMAIL, email);
        startActivity(homeActivity);
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
                facebookLogin();
                break;
            case R.id.btn_google_login:
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
}

