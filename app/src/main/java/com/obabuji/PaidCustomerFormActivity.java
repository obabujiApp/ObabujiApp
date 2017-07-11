package com.obabuji;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class PaidCustomerFormActivity extends AppCompatActivity {

    private AppCompatEditText et_name, et_email, et_company, et_mobile,et_otp;
    private Button btn_submit,btn_complaint;
    private Context ctx;
    private LinearLayout form_layout,parent_layout;
    private TextView tv_message;
    private  ActionBar actionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_paid_customer_form);

        actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.paid_customer));
        }

        ctx = PaidCustomerFormActivity.this;

        findViewById();

        actionCheckStatus();

        btn_submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String email = et_email.getText().toString().trim();
                String mobile = et_mobile.getText().toString().trim();
                String otp = et_otp.getText().toString().trim();

                if(email.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Enter email id");
                }else if(!AppUtils.isValidEmail(email)){
                    AppUtils.showToast(ctx,"Invalid email id");
                }else if(mobile.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Enter mobile no");
                }else if(mobile.length()<10){
                    AppUtils.showToast(ctx,"Invalid mobile no");
                }else if(otp.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Enter OTP");
                }else{
                    actionLogin(mobile,email,otp);
                }

            }
        });

        btn_complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,ComplaintActivity.class);
                startActivity(intent);
            }
        });
    }

    public void findViewById(){
        et_name = (AppCompatEditText) findViewById(R.id.et_name);
        et_email = (AppCompatEditText) findViewById(R.id.et_email);
        et_company = (AppCompatEditText) findViewById(R.id.et_company);
        et_mobile = (AppCompatEditText) findViewById(R.id.et_mobile);
        et_otp = (AppCompatEditText) findViewById(R.id.et_otp);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_complaint = (Button) findViewById(R.id.btn_complaint);
        tv_message = (TextView) findViewById(R.id.tv_message);
        form_layout = (LinearLayout) findViewById(R.id.form_layout);
        parent_layout = (LinearLayout) findViewById(R.id.parent_layout);
    }

    public void showDetail(){
        RegisterUser registerUser = SessionManager.getInstance(ctx).getUserInfo();

        et_name.setText(registerUser.getName());
        et_email.setText(registerUser.getEmail());
        et_mobile.setText(registerUser.getPhone());
        et_company.setText(registerUser.getCompany());
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private void actionLogin(final String mobile,final String email,final String otp){

        if (!AppUtils.isNetworkAvailable(ctx)) {
            return;
        }

        if (MyApplication.getInstance().getApiEndpoint() == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String id = SessionManager.getInstance(ctx).getUserInfo().getId();

        Call<String> loginCall = MyApplication.getInstance()
                .getApiEndpoint().paidUserLogin(id,email,mobile,otp);

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

//            Log.e("Obabuji","response= "+response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){
                AppUtils.showToast(ctx, jsonObject.getString("message"));
                form_layout.setVisibility(View.GONE);
                tv_message.setVisibility(View.VISIBLE);
            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void actionCheckStatus(){

        if (!AppUtils.isNetworkAvailable(ctx)) {
            return;
        }

        if (MyApplication.getInstance().getApiEndpoint() == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String id = SessionManager.getInstance(ctx).getUserInfo().getId();

        Call<String> loginCall = MyApplication.getInstance()
                .getApiEndpoint().checkStatus(id);

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {

                    if (response.isSuccessful() && response.body() != null) {

                        parseCheckStatusResponse(response.body().toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("obabuji", "onFailure: " + t.getMessage());
                showDetail();
                progressDialog.hide();
            }
        });
    }

    private void parseCheckStatusResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){

                JSONArray userStatus  = jsonObject.getJSONArray("user_status");
                JSONObject status = userStatus.getJSONObject(0);
                String isActive = status.getString("is_active");
                String isPaidLogin = status.getString("is_paid_login");

                if(isPaidLogin.equalsIgnoreCase("0")){
                    form_layout.setVisibility(View.VISIBLE);
                    showDetail();
                }else if(isActive.equalsIgnoreCase("1")){
                    //active
                    tv_message.setVisibility(View.VISIBLE);
                    tv_message.setText("Congratulation your are active member. But you do'nt have any service now");
                    actionGetMyService();
                }else{
                    tv_message.setVisibility(View.VISIBLE);
                }

            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
                showDetail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void actionGetMyService(){

        if (!AppUtils.isNetworkAvailable(ctx)) {
            return;
        }

        if (MyApplication.getInstance().getApiEndpoint() == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        String id = SessionManager.getInstance(ctx).getUserInfo().getId();

        Call<String> loginCall = MyApplication.getInstance()
                .getApiEndpoint().getServices(id);

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {

                    if (response.isSuccessful() && response.body() != null) {

                        parseGetMyService(response.body().toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("obabuji", "onFailure: " + t.getMessage());
                showDetail();
                progressDialog.hide();
            }
        });
    }

    private void parseGetMyService(String response){

        try {

            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){

                JSONArray serviceList  = jsonObject.getJSONArray("service_list");

                if(serviceList.length()>0) {

                    actionBar.setTitle(getResources().getString(R.string.my_service));
                    tv_message.setVisibility(View.GONE);
                    btn_complaint.setVisibility(View.VISIBLE);

                    for (int s = 0; s < serviceList.length(); s++) {

                         JSONObject service = serviceList.getJSONObject(s);
                        View child = getLayoutInflater().inflate(R.layout.my_service_item, null);
                        TextView tv_service_name = (TextView) child.findViewById(R.id.tv_service_name);

                        final String serviceName=service.getString("service_name");
                        final String startDate=service.getString("created_date");
                        final String renewDate=service.getString("renewal_date");
                        tv_service_name.setText(serviceName);

                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                        layoutParams.setMargins(10, 10, 10, 0);

                        child.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(ctx,MyServiceActivity.class);
                                intent.putExtra("name",serviceName);
                                intent.putExtra("started Date",startDate);
                                intent.putExtra("renewal Date",renewDate);
                                startActivity(intent);
                            }
                        });

                        parent_layout.addView(child,layoutParams);
                    }

/*
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    layoutParams.setMargins(10, 20, 10, 0);


                    Button btnComplaint  = new Button(ctx);
                    btnComplaint.setText("Complaint");
                    btnComplaint.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    btnComplaint.setTextColor(Color.WHITE);

                    parent_layout.addView(btnComplaint,layoutParams);

                    btnComplaint.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });*/

                }

            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
                showDetail();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
