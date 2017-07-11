package com.obabuji;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.obabuji.adapter.NotificationAdapter;
import com.obabuji.app.MyApplication;
import com.obabuji.model.ItemNotification;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private Context ctx;
    private NotificationAdapter notificationAdapter;
    private ArrayList<ItemNotification> itemNotifications = new ArrayList<>();
    private RecyclerView recyclerView_notification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);

        recyclerView_notification = (RecyclerView) findViewById(R.id.recyclerView_notification);

        ctx = NotificationActivity.this;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.notification));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView_notification.setLayoutManager(layoutManager);

        actionGetNotification();

       SessionManager.getInstance(ctx).saveNotificationCount("0");

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(0);
    }

    private void actionGetNotification(){

        if (!AppUtils.isNetworkAvailable(ctx)) {
            AppUtils.showToast(ctx, getString(R.string.no_internet));
            return;
        }

        if (MyApplication.getInstance().getApiEndpoint() == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<String> loginCall = MyApplication.getInstance()
                .getApiEndpoint().getNotifications();

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        parseResponse(response.body().toString());
                    }else{
                        AppUtils.showToast(ctx, "Failed try again");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.d("sewa", "onFailure: " + t.getMessage());
                progressDialog.hide();
            }
        });
    }

    private void parseResponse(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);

            itemNotifications.clear();

            if(jsonObject.getString("status").equalsIgnoreCase("1")){

                JSONArray allNotifications = jsonObject.getJSONArray("all_notification");

                for(int s=0;s<allNotifications.length();s++) {

                    JSONObject notification = allNotifications.getJSONObject(s);

                    ItemNotification item = new ItemNotification();
                    item.setMessage(notification.getString("notification_des"));
                    String date= AppUtils.changeDatetimeFormatCoupon("yyyy-MM-dd",notification.getString("created_date"));
                    item.setDate(date);
                    itemNotifications.add(item);
                }

                notificationAdapter = new NotificationAdapter(ctx,itemNotifications);
                recyclerView_notification.setAdapter(notificationAdapter);

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
