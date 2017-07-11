package com.obabuji;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
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
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.obabuji.app.MyApplication;
import com.obabuji.model.RegisterUser;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ImageView fb,gp,linkedin;
    private CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN=124;
    private AppCompatEditText et_email_mobile,et_password;
    private Button btn_login;
    private Context ctx;
    private String fcmID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fb = (ImageView) findViewById(R.id.fb);
        gp = (ImageView) findViewById(R.id.gp);
        linkedin = (ImageView) findViewById(R.id.linkedin);
        btn_login = (Button) findViewById(R.id.btn_login);
        et_email_mobile = (AppCompatEditText) findViewById(R.id.et_email_mobile);
        et_password = (AppCompatEditText) findViewById(R.id.et_password);

        ctx = LoginActivity.this;

        FirebaseApp.initializeApp(this);

        fcmID = FirebaseInstanceId.getInstance().getToken();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_mobile = et_email_mobile.getText().toString().trim();
                String  password = et_password.getText().toString().trim();

                if (email_mobile.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter registered email/mobile");
                }else if (password.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter password");
                }else{
                    actionLogin(email_mobile,password);

                }
            }
        });

        callbackManager = CallbackManager.Factory.create();

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile","email"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.i("LoginActivity", response.toString());
                        // Get facebook data from login
                        Bundle bFacebookData = getFacebookData(object);

                        Log.e("obabuji","fb data name="+bFacebookData.getString("first_name")+""+bFacebookData.getString("last_name")+"\nemail="+bFacebookData.getString("email")+"\ngender="+bFacebookData.getString("gender"));

                        actionSignUp(bFacebookData.getString("first_name")+" "+bFacebookData.getString("last_name"),bFacebookData.getString("email"),bFacebookData.getString("profile_pic"));

                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, first_name, last_name, email,gender"); // Parámetros que pedimos a facebook
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        gp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mGoogleApiClient.isConnected()){
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                }
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        initGoogle();
    }

    private void actionLogin(final String email_mobile, final String password){

        if (!AppUtils.isNetworkAvailable(ctx)) {
            return;
        }

        if (MyApplication.getInstance().getApiEndpoint() == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(fcmID.equalsIgnoreCase("")){
            fcmID = FirebaseInstanceId.getInstance().getToken();
        }

        Call<String> loginCall = MyApplication.getInstance()
                .getApiEndpoint().userLogin(email_mobile,password,fcmID,email_mobile);

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {

                    if (response.isSuccessful() && response.body() != null) {

                        parseResponse(response.body().toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("obabuji", "onFailure: " + t.getMessage());
                progressDialog.hide();
            }
        });
    }

    private void parseResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);

            Log.e("Obabuji","response= "+response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){
                JSONArray jsonArray = jsonObject.getJSONArray("user_details");

                JSONObject user_details = jsonArray.getJSONObject(0);

                String id = user_details.getString("user_id");
                RegisterUser registerUser = new RegisterUser();
                registerUser.setId(id);
                registerUser.setName(user_details.getString("name"));
                registerUser.setPhone(user_details.getString("mob"));
                registerUser.setEmail(user_details.getString("email"));
                registerUser.setCompany(user_details.getString("company"));
                registerUser.setProfile_pic(user_details.getString("profile_pic"));
                SessionManager.getInstance(ctx).saveUserInfo(registerUser);

                Intent intent = new Intent(ctx,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void initGoogle(){
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
// basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

// Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new  GoogleApiClient.OnConnectionFailedListener(){

                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private Bundle getFacebookData(JSONObject object) {

        try {
            Bundle bundle = new Bundle();
            String id = object.getString("id");

            try {
                URL profile_pic = new URL("https://graph.facebook.com/" + id + "/picture?width=200&height=150");
                Log.i("profile_pic", profile_pic + "");
                bundle.putString("profile_pic", profile_pic.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }

            bundle.putString("idFacebook", id);
            if (object.has("first_name"))
                bundle.putString("first_name", object.getString("first_name"));
            if (object.has("last_name"))
                bundle.putString("last_name", object.getString("last_name"));
            if (object.has("email"))
                bundle.putString("email", object.getString("email"));
            if (object.has("gender"))
                bundle.putString("gender", object.getString("gender"));

            return bundle;
        } catch (JSONException e) {
            Log.d("obabuji", "Error parsing JSON");
        }

        return null;
    }


    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from
        //   GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                // Get account information
                String mFullName = acct.getDisplayName();

                String mEmail = acct.getEmail();

                String imageUrl = acct.getPhotoUrl()==null?"":acct.getPhotoUrl().toString();

                Log.e("sa","email info name="+mFullName+" email-"+mEmail+" \nimgUrl= "+imageUrl);

                actionSignUp(mFullName,mEmail,imageUrl);
            }
        }
    }



    private void actionSignUp(final String name, final String email, final String imageUrl){

        if (!AppUtils.isNetworkAvailable(ctx)) {
            return;
        }

        if (MyApplication.getInstance().getApiEndpoint() == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        if(fcmID.equalsIgnoreCase("")){
            fcmID = FirebaseInstanceId.getInstance().getToken();
        }

        Call<String> loginCall = MyApplication.getInstance()
                .getApiEndpoint().signUp(name,"",fcmID,email,"",1, imageUrl);

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {

                    if (response.isSuccessful() && response.body() != null) {

                        parseResponseSignup(response.body().toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("obabuji", "onFailure: " + t.getMessage());
                progressDialog.hide();
            }
        });
    }

    private void parseResponseSignup(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){
                JSONArray jsonArray = jsonObject.getJSONArray("user_details");

                JSONObject user_details = jsonArray.getJSONObject(0);

                String id = user_details.getString("user_id");
                RegisterUser registerUser = new RegisterUser();
                registerUser.setId(id);
                registerUser.setName(user_details.getString("name"));
                registerUser.setPhone(user_details.getString("mob"));
                registerUser.setEmail(user_details.getString("email"));
                registerUser.setCompany(user_details.getString("company"));
                registerUser.setProfile_pic(user_details.getString("profile_pic"));
                SessionManager.getInstance(ctx).saveUserInfo(registerUser);

                Intent intent = new Intent(ctx,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void onSignup(View view){
        Intent intent = new Intent(this,SignupActivity.class);
        startActivity(intent);
    }

    public void onForgotPassword(View view){
        Intent intent = new Intent(this,ForgotPasswordActivity.class);
        startActivity(intent);
    }
    public void onLogin(View view){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
