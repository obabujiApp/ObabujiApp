package com.obabuji;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.obabuji.app.MyApplication;
import com.obabuji.model.RegisterUser;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context ctx;
    private AppCompatEditText et_name, et_email, et_mobile, et_password, et_confirm_password;
    private Button btn_signup;
    private String fcmID="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById();

        ctx = SignupActivity.this;

        FirebaseApp.initializeApp(this);

        fcmID = FirebaseInstanceId.getInstance().getToken();

/*        toolbar = (Toolbar) findViewById(R.id.toolbar_top);
        setSupportActionBar(toolbar);*/
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.btn_signup));
          /*  TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            Typeface font = Typeface.createFromAsset(MyApplication.getInstance().getAssets(),"fonts/Xerox_Serif_Narrow.ttf");
            toolbarTitle.setTypeface(font);
            toolbarTitle.setText(getResources().getString(R.string.btn_signup));*/

        }

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = et_name.getText().toString().trim();
                String email = et_email.getText().toString().trim();
                String mobile = et_mobile.getText().toString().trim();
                String password = et_password.getText().toString().trim();
                String confirm_password = et_confirm_password.getText().toString().trim();


                if (name.equalsIgnoreCase("")) {
                    AppUtils.showToast(ctx, "Please enter name");
                } else if (mobile.equalsIgnoreCase("")) {
                    AppUtils.showToast(ctx, "Please enter mobile number");
                } else if (mobile.length() < 10) {
                    AppUtils.showToast(ctx, "Invalid mobile number");
                } else if (email.equalsIgnoreCase("")) {
                    AppUtils.showToast(ctx, "Please enter email id");
                } else if (!AppUtils.isValidEmail(email)) {
                    AppUtils.showToast(ctx, "Invalid email id");
                } else if (password.equalsIgnoreCase("")) {
                    AppUtils.showToast(ctx, "Please enter password");
                } else if (confirm_password.equalsIgnoreCase("")) {
                    AppUtils.showToast(ctx, "Please enter confirm password");
                } else if (!password.equals(confirm_password)) {
                    AppUtils.showToast(ctx, "Password does not match");
                } else {
                    actionSignUp(name, mobile, email, password);
                }
            }
        });

    }

    private  void findViewById(){
        et_name = (AppCompatEditText) findViewById(R.id.et_name);
        et_email = (AppCompatEditText) findViewById(R.id.et_email);
        et_mobile = (AppCompatEditText) findViewById(R.id.et_mobile);
        et_password = (AppCompatEditText) findViewById(R.id.et_password);
        et_confirm_password = (AppCompatEditText) findViewById(R.id.et_confirm_password);
        btn_signup = (Button) findViewById(R.id.btn_signup);

    }

    private void actionSignUp(final String name, final String mobile, final String email, final String password){

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
                .getApiEndpoint().signUp(name,mobile,fcmID,email,password,0, "");

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {

                    if (response.isSuccessful() && response.body() != null) {

                        parseResponse(name,mobile,response.body().toString(),email,"");
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

    private void parseResponse(String name,String mobile,String response, String email,String profile_pic){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){
                JSONArray jsonArray = jsonObject.getJSONArray("user_details");

                JSONObject user_details = jsonArray.getJSONObject(0);

                String id = user_details.getString("user_id");
                RegisterUser registerUser = new RegisterUser();
                registerUser.setId(id);
                registerUser.setName(name);
                registerUser.setPhone(mobile);
                registerUser.setEmail(email);
                registerUser.setProfile_pic(profile_pic);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
