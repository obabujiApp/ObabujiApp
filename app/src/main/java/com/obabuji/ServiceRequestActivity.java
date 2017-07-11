package com.obabuji;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.obabuji.app.MyApplication;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceRequestActivity extends AppCompatActivity {

    private Context ctx;
    private String selectedServices="";
    private CheckBox cb_digital_marketing,cb_pay_per_click,cb_social_media_marketing,cb_youtube_advertising,cb_mobile_marketing,cb_seo,cb_content_marketing,cb_email_marketing,cb_crm,cb_web_design;
    private TextView tv_digital_marketing,tv_pay_per_click,tv_social_media_marketing,tv_youtube_advertising,tv_mobile_marketing,tv_seo,tv_content_marketing,tv_email_marketing,tv_crm,tv_web_design;
    private ArrayList<String> list = new ArrayList<>();
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_request);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.request_service));
        }

        ctx = ServiceRequestActivity.this;

        findViewById();

        selectServices();


    }

    public void selectServices(){

        cb_digital_marketing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_digital_marketing.getText().toString());

                }else{

                    list.remove(tv_digital_marketing.getText().toString());
                }
            }
        });

        cb_pay_per_click.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_pay_per_click.getText().toString());

                }else{

                    list.remove(tv_pay_per_click.getText().toString());

                }
            }
        });

        cb_social_media_marketing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_social_media_marketing.getText().toString());

                }else{

                    list.remove(tv_social_media_marketing.getText().toString());

                }
            }
        });

        cb_youtube_advertising.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_youtube_advertising.getText().toString());

                }else{

                    list.remove(tv_youtube_advertising.getText().toString());

                }
            }
        });

        cb_mobile_marketing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_mobile_marketing.getText().toString());

                }else{

                    list.remove(tv_mobile_marketing.getText().toString());

                }
            }
        });

        cb_seo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_seo.getText().toString());

                }else{

                    list.remove(tv_seo.getText().toString());

                }
            }
        });

        cb_content_marketing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_content_marketing.getText().toString());

                }else{

                    list.remove(tv_content_marketing.getText().toString());

                }
            }
        });

        cb_email_marketing.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_email_marketing.getText().toString());

                }else{

                    list.remove(tv_email_marketing.getText().toString());

                }
            }
        });

        cb_crm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_crm.getText().toString());

                }else{

                    list.remove(tv_crm.getText().toString());

                }
            }
        });

        cb_web_design.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    list.add(tv_web_design.getText().toString());

                }else{

                    list.remove(tv_web_design.getText().toString());

                }
            }
        });



        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                 if(list.size()>0) {
                     selectedServices = android.text.TextUtils.join(",", list);
                     actionGetMyService(selectedServices);
                 }else{
                     AppUtils.showToast(ctx,"Please select at least one service.");
                 }
            }
        });

    }

    public void findViewById(){

        tv_content_marketing = (TextView) findViewById(R.id.tv_content_marketing);
        tv_crm = (TextView) findViewById(R.id.tv_crm);
        tv_digital_marketing = (TextView) findViewById(R.id.tv_digital_marketing);
        tv_email_marketing = (TextView) findViewById(R.id.tv_email_marketing);
        tv_mobile_marketing = (TextView) findViewById(R.id.tv_mobile_marketing);
        tv_pay_per_click = (TextView) findViewById(R.id.tv_pay_per_click);
        tv_seo = (TextView) findViewById(R.id.tv_seo);
        tv_social_media_marketing = (TextView) findViewById(R.id.tv_social_media_marketing);
        tv_web_design = (TextView) findViewById(R.id.tv_web_design);
        tv_youtube_advertising = (TextView) findViewById(R.id.tv_youtube_advertising);
        cb_content_marketing = (CheckBox) findViewById(R.id.cb_content_marketing);
        cb_crm = (CheckBox) findViewById(R.id.cb_crm);
        cb_digital_marketing = (CheckBox) findViewById(R.id.cb_digital_marketing);
        cb_email_marketing = (CheckBox) findViewById(R.id.cb_email_marketing);
        cb_mobile_marketing = (CheckBox) findViewById(R.id.cb_mobile_marketing);
        cb_pay_per_click = (CheckBox) findViewById(R.id.cb_pay_per_click);
        cb_seo = (CheckBox) findViewById(R.id.cb_seo);
        cb_social_media_marketing = (CheckBox) findViewById(R.id.cb_social_media_marketing);
        cb_web_design = (CheckBox) findViewById(R.id.cb_web_design);
        cb_youtube_advertising = (CheckBox) findViewById(R.id.cb_youtube_advertising);
        btn = (Button) findViewById(R.id.btn_request_service);

    }


    private void actionGetMyService(final String services){

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
                .getApiEndpoint().requestServices(id,services);

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
                progressDialog.hide();
            }
        });
    }

    private void parseGetMyService(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){

                AppUtils.alert(ctx,"Your request has been successfully sent. We will contact you very soon!");

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
