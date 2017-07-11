package com.obabuji;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRatingBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
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

public class FeedbackAndSuggestionActivity extends AppCompatActivity {

    private AppCompatRatingBar rb_helpful;
    private RatingBar rb_ease_of_use,rb_design;
    private TextView tv_helpful,tv_ease_of_use,tv_design;
    private AppCompatEditText et_comment;
    private Button btn_submit;
    private Context ctx;
    private ArrayList<String> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_and_suggestion);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.feedback_and_suggestion));
        }

        ctx = FeedbackAndSuggestionActivity.this;

        findViewById();

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = et_comment.getText().toString();
                String helpful = ""+rb_helpful.getRating();
                String design = ""+rb_design.getRating();
                String use = ""+rb_ease_of_use.getRating();

                actionFeedback(feedback,helpful,use,design);
            }
        });

    }

    private void actionFeedback(final String feedback, final String helful,final String use,final String design){

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
                .getApiEndpoint().addFeedback(id,feedback,helful,use,design);

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
                progressDialog.hide();
            }
        });
    }

    private void parseCheckStatusResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            if(jsonObject.getString("status").equalsIgnoreCase("1")){
                AppUtils.alert(ctx,"Thank you for taking the time to provide us with your feedback.");
            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void findViewById(){
        rb_helpful = (AppCompatRatingBar) findViewById(R.id.rb_helpful);
        rb_ease_of_use = (RatingBar) findViewById(R.id.rb_ease_of_use);
        rb_design = (RatingBar) findViewById(R.id.rb_design);
        et_comment = (AppCompatEditText) findViewById(R.id.et_comment);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        tv_helpful = (TextView) findViewById(R.id.tv_helpful);
        tv_ease_of_use = (TextView) findViewById(R.id.tv_ease_of_use);
        tv_design = (TextView) findViewById(R.id.tv_design);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
