package com.obabuji;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;

import com.obabuji.app.MyApplication;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmailMarketingActivity extends AppCompatActivity {

    private WebView webview;
    private Button btn_request_service;
    private Context ctx;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_marketing);
        webview  = (WebView) findViewById(R.id.webview);
        btn_request_service = (Button) findViewById(R.id.btn_request_service);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.email_marketing));
        }
        ctx = EmailMarketingActivity.this;

        btn_request_service.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                actionGetMyService(getResources().getString(R.string.email_marketing));

            }
        });


        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebViewClient(new AppWebViewClients());
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webview.loadUrl("file:///android_asset/managed-email-marketing.html");

    }

    private class AppWebViewClients extends WebViewClient {

        public AppWebViewClients() {

            progressBar.setVisibility(View.VISIBLE);
        }



        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(view.getUrl());
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
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
