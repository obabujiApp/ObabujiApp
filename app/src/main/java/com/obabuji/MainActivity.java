package com.obabuji;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.obabuji.adapter.SliderAdapter;
import com.obabuji.model.RegisterUser;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private  ViewPager mPager;
    private static int currentPage = 0;
    private static final String[] XMEN= {"https://www.obabuji.com/obabuji-images/Digital-Marketing-Company.jpg","https://www.obabuji.com/obabuji-images/Digital-Marketing-in-India.jpg","https://www.obabuji.com/obabuji-images/Digital-Marketing-services.jpg","https://www.obabuji.com/obabuji-images/digital-marketing-agency-india.jpg","https://www.obabuji.com/obabuji-images/Online-Marketing-Company.jpg","https://www.obabuji.com/obabuji-images/Banner_16.jpg"};
    private ArrayList<String> XMENArray = new ArrayList<String>();
    private Context ctx;
    private CardView layout_1,layout_2,layout_3,layout_4,layout_5,layout_6,layout_7,layout_8,layout_9,layout_10;
    private TextView tv_name,tv_email;
    private CircleImageView profile_image;
    private Menu mMenu;
    private final int READ_NOTIFICATION=1254;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findLayoutId();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ctx = MainActivity.this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initSlider();

        layoutClickListener();


        if(getIntent().hasExtra("type")){
            String type = getIntent().getStringExtra("type");

            if(type.equalsIgnoreCase("0")){
                Intent intent = new Intent(this,NotificationActivity.class);
                startActivity(intent);
            }

            if(type.equalsIgnoreCase("1")){
                Intent intent = new Intent(this,ComplaintActivity.class);
                startActivity(intent);
            }

        }

        //        createHashKey();
    }

    private void layoutClickListener(){

        layout_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,DigitalMarketingActivity.class);
                startActivity(intent);
            }
        });

        layout_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,PayPerClickActivity.class);
                startActivity(intent);
            }
        });

        layout_3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,SocialMediaMarketingActivity.class);
                startActivity(intent);
            }
        });

        layout_4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,YoutubeAdvertisingActivity.class);
                startActivity(intent);
            }
        });

        layout_5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,MobileMarketingActivity.class);
                startActivity(intent);
            }
        });

        layout_6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,SeoActivity.class);
                startActivity(intent);
            }
        });

        layout_7.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,ContentMarketingActivity.class);
                startActivity(intent);
            }
        });

        layout_8.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,EmailMarketingActivity.class);
                startActivity(intent);
            }
        });

        layout_9.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,CrmActivity.class);
                startActivity(intent);
            }
        });

        layout_10.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx,WebDesignActivity.class);
                startActivity(intent);
            }
        });

    }


    private void findLayoutId(){

        layout_1 = (CardView) findViewById(R.id.layout_1);
        layout_2 = (CardView) findViewById(R.id.layout_2);
        layout_3 = (CardView) findViewById(R.id.layout_3);
        layout_4 = (CardView) findViewById(R.id.layout_4);
        layout_5 = (CardView) findViewById(R.id.layout_5);
        layout_6 = (CardView) findViewById(R.id.layout_6);
        layout_7 = (CardView) findViewById(R.id.layout_7);
        layout_8 = (CardView) findViewById(R.id.layout_8);
        layout_9 = (CardView) findViewById(R.id.layout_9);
        layout_10 = (CardView) findViewById(R.id.layout_10);
    }




   private void createHashKey(){

       try {
           PackageInfo info = getPackageManager().getPackageInfo(
                   "com.obabuji",
                   PackageManager.GET_SIGNATURES);
           for (Signature signature : info.signatures) {
               MessageDigest md = MessageDigest.getInstance("SHA");
               md.update(signature.toByteArray());
               Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
           }
       } catch (PackageManager.NameNotFoundException e) {

       } catch (NoSuchAlgorithmException e) {

       }

   }


    private void initSlider() {
        for(int i=0;i<XMEN.length;i++)
            XMENArray.add(XMEN[i]);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SliderAdapter(ctx,XMENArray));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == XMEN.length) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 3000, 3000);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_service) {
            // Handle the camera action
            Intent intent = new Intent(this,PaidCustomerFormActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_request_service) {
            Intent intent = new Intent(this,ServiceRequestActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_my_profile) {
            Intent intent = new Intent(this,EditProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_notification) {
            Intent intent = new Intent(this,NotificationActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(this,AboutUsActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_contact_us) {
            Intent intent = new Intent(this,ContactUsActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_feedback) {
            Intent intent = new Intent(this,FeedbackAndSuggestionActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_change_password) {
            Intent intent = new Intent(this,KotlinDemoActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_logout) {

            AppUtils.logout(ctx);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setNotificationCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notification_menu, menu);//Menu Resource, Menu

        mMenu = menu;

        MenuItem itemMessages = menu.findItem(R.id.notification);

       RelativeLayout badgeLayout = (RelativeLayout) itemMessages.getActionView();
       TextView  itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        ImageView iconButtonMessages =(ImageView) badgeLayout.findViewById(R.id.badge_icon_button);
        itemMessagesBadgeTextView.setVisibility(View.GONE); // initially hidden


        String notificationCount = SessionManager.getInstance(ctx).getNotificationCount();
        if(!notificationCount.equalsIgnoreCase("0")){
            itemMessagesBadgeTextView.setText(notificationCount);
            itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
        }

        iconButtonMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ctx,NotificationActivity.class);
                startActivity(intent);
            }
        });
        showDetail();
        return true;
    }

    private void setNotificationCount(){

        if(mMenu==null){
            return;
        }
        MenuItem itemMessages = mMenu.findItem(R.id.notification);
        RelativeLayout badgeLayout = (RelativeLayout) itemMessages.getActionView();
        TextView  itemMessagesBadgeTextView = (TextView) badgeLayout.findViewById(R.id.badge_textView);
        itemMessagesBadgeTextView.setVisibility(View.GONE); // initially hidden

        String notificationCount = SessionManager.getInstance(ctx).getNotificationCount();
        if(!notificationCount.equalsIgnoreCase("0")){
            itemMessagesBadgeTextView.setText(notificationCount);
            itemMessagesBadgeTextView.setVisibility(View.VISIBLE);
        }
    }

    public void showDetail(){
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_email = (TextView) findViewById(R.id.tv_email);
        profile_image = (CircleImageView) findViewById(R.id.profile_image);

        RegisterUser registerUser = SessionManager.getInstance(ctx).getUserInfo();

        tv_name.setText(registerUser.getName());
        tv_email.setText(registerUser.getEmail());

        if (!registerUser.getProfile_pic().equalsIgnoreCase("")) {
            Glide.with(ctx).load(registerUser.getProfile_pic()).into(profile_image);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.notification){
            Intent intent = new Intent(ctx,NotificationActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
