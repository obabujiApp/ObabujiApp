package com.obabuji;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.obabuji.utils.AppUtils;

public class ForgotPasswordActivity extends AppCompatActivity {

    private AppCompatEditText et_mobile, et_enter_otp, et_enter_password, et_confirm_password;
    private Context ctx;
    private TextView tv_send_otp;
    private Button btn_reset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.btn_resetPassword));
        }

        ctx = ForgotPasswordActivity.this;
        findViewById();

        tv_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = et_mobile.getText().toString().trim();

                if (mobile.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter mobile");
                }else if (mobile.length()<10){
                    AppUtils.showToast(ctx,"Invalid mobile number");
                }else {

                }
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = et_mobile.getText().toString().trim();
                String otp = et_enter_otp.getText().toString().trim();
                String password = et_enter_password.getText().toString().trim();
                String confirm_password = et_confirm_password.getText().toString().trim();

                if (mobile.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter mobile");
                }else if (mobile.length()<10){
                    AppUtils.showToast(ctx,"Invalid mobile number");
                }else if (otp.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter OTP");
                }else if (password.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter new password");
                }else if (confirm_password.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter confirm password");
                }else if (!password.equalsIgnoreCase(confirm_password)){
                    AppUtils.showToast(ctx,"Password does not match");
                }else {

                }
            }
        });
    }

    public void findViewById(){

        et_mobile = (AppCompatEditText) findViewById(R.id.et_mobile);
        tv_send_otp = (TextView) findViewById(R.id.tv_send_otp);
        et_enter_otp = (AppCompatEditText) findViewById(R.id.et_enter_otp);
        et_enter_password = (AppCompatEditText) findViewById(R.id.et_enter_password);
        et_confirm_password = (AppCompatEditText) findViewById(R.id.et_confirm_password);
        btn_reset = (Button) findViewById(R.id.btn_reset);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
