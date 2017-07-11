package com.obabuji;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.obabuji.adapter.ChatAdapter;
import com.obabuji.app.MyApplication;
import com.obabuji.model.ChatItem;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ComplaintActivity extends AppCompatActivity {

    private Context ctx;
    private ImageView btn_send;
    private EditText et_message;
    private ChatAdapter chatAdapter;
    private ArrayList<ChatItem> chatItemArrayList = new ArrayList<>();
    private RecyclerView recyclerView_chat;
    private LinearLayoutManager layoutManager;
    private RelativeLayout layout_bottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);

        ctx = ComplaintActivity.this;

        findViewByIds();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Complaint / Query");
        }


        setUpAdapter();

        actionGetAllMessage();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message= et_message.getText().toString().trim();
                if(message.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please type message");
                }else{
                    actionSendTextMessage(message);
                }
            }
        });

    }


    private void actionGetAllMessage(){

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
                .getApiEndpoint().getAllMessage(id,"0");

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {

                    if (response.isSuccessful() && response.body() != null) {

                        parseAllMessage(response.body().toString());
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

    private void parseAllMessage(String response){

        try {

            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){

                JSONArray allMessage = jsonObject.getJSONArray("all_complaint");
                String myId = SessionManager.getInstance(ctx).getUserInfo().getId();

                for(int s=0;s<allMessage.length();s++){

                    JSONObject singleMessage = allMessage.getJSONObject(s);

                    ChatItem chatItem = new ChatItem();
                    chatItem.setMessage(singleMessage.getString("complaint_msg"));
                    chatItem.setDatetime(singleMessage.getString("created_msg_date"));
                    chatItem.setReceiverId(singleMessage.getString("receiver_id"));
                    chatItem.setSenderId(singleMessage.getString("sender_id"));

                    if(singleMessage.getString("sender_id").equalsIgnoreCase(myId)){
                        chatItem.setLeftOrRight(0);

                    }else{
                        chatItem.setLeftOrRight(1);
                    }

                    chatItemArrayList.add(chatItem);
                }

               /* ChatItem lastItem = chatItemArrayList.get(chatItemArrayList.size()-1);

                if(lastItem.getLeftOrRight()==0){
                    layout_bottom.setVisibility(View.GONE);
                }*/

                chatAdapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(chatItemArrayList.size()-1);

            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

        private void actionSendTextMessage(final String message){

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

        String myId = SessionManager.getInstance(ctx).getUserInfo().getId();

        Call<String> loginCall = MyApplication.getInstance()
                .getApiEndpoint().sendTextMessage(message,myId,"0");

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {
                    if (response.isSuccessful() && response.body() != null) {
                        parseTextMessageResponse(response.body().toString(),message);
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

    private void parseTextMessageResponse(String response,String message){

        try {
            JSONObject jsonObject = new JSONObject(response);

            if(jsonObject.getString("status").equalsIgnoreCase("1")){
                String datetime=jsonObject.getString("created_complaint_date");
                ChatItem chatItem = new ChatItem();
                chatItem.setDatetime(datetime);
                chatItem.setMessage(message);
                chatItem.setLeftOrRight(0);

                chatItemArrayList.add(chatItem);
                chatAdapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(chatItemArrayList.size()-1);
                et_message.setText("");
            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void setUpAdapter(){

        chatAdapter = new ChatAdapter(ctx, chatItemArrayList);
        layoutManager = new LinearLayoutManager(ctx);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView_chat.setLayoutManager(layoutManager);
        recyclerView_chat.setAdapter(chatAdapter);
    }


    private void findViewByIds(){
        recyclerView_chat = (RecyclerView) findViewById(R.id.recyclerView_chat);
        et_message = (EditText) findViewById(R.id.et_message);
        btn_send = (ImageView) findViewById(R.id.btn_send);
        layout_bottom = (RelativeLayout) findViewById(R.id.layout_bottom);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}
