package com.obabuji;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.obabuji.model.RegisterUser;
import com.obabuji.session.SessionManager;

public class SplashActivity extends AppCompatActivity {

    String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {

            if (bundle.containsKey("type")) {
                type = bundle.getString("type");
            }
        }

        Handler handler = new Handler();

        Runnable runnable = new Runnable() {

            @Override
            public void run() {

                SessionManager sessionManager = SessionManager.getInstance(SplashActivity.this);
                RegisterUser registerUser = sessionManager.getUserInfo();

                if (registerUser!=null && !registerUser.getEmail().equalsIgnoreCase("")){
                    Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                    intent.putExtra("type",type);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 3000);
    }
}
