package com.obabuji.app;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.FirebaseApp;
import com.obabuji.utils.ApiEndpoint;
import com.obabuji.utils.AppConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by user on 26-06-2017.
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private ApiEndpoint apiEndpoint;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        FirebaseApp.initializeApp(mInstance);
        Fresco.initialize(getApplicationContext());
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Xerox_Serif_Narrow.ttf");
    }

    public static synchronized MyApplication getInstance() {
        if (mInstance == null) {
            mInstance = new MyApplication();
        }
        return mInstance;
    }


    private void initRetrofitConfig() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(AppConstants.SERVICE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        apiEndpoint = retrofit.create(ApiEndpoint.class);
    }

    public ApiEndpoint getApiEndpoint() {
        if (apiEndpoint == null) {
            initRetrofitConfig();
        }
        return apiEndpoint;
    }
}
