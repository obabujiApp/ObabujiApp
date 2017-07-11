package com.obabuji;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.obabuji.utils.AppUtils;

public class MyServiceActivity extends AppCompatActivity {

    private TextView tv_service_name,tv_started_date,tv_renewal_date;
    private String name="",startedDate="",renewalDate="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_service);

        tv_service_name = (TextView) findViewById(R.id.tv_service_name);
        tv_started_date = (TextView) findViewById(R.id.tv_started_date);
        tv_renewal_date = (TextView) findViewById(R.id.tv_renewal_date);

        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.service_detail));
        }

        name = getIntent().getStringExtra("name");
        startedDate = getIntent().getStringExtra("started Date");
        renewalDate = getIntent().getStringExtra("renewal Date");

        tv_service_name.setText(name);
        tv_started_date.setText(AppUtils.changeDatetimeFormat("yyyy-MM-dd",startedDate));
        tv_renewal_date.setText(AppUtils.changeDatetimeFormat("yyyy-MM-dd",renewalDate));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
