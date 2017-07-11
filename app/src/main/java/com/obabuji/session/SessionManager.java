package com.obabuji.session;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.obabuji.model.RegisterUser;


public class SessionManager {
    private static final String TAG = SessionManager.class.getSimpleName();
    private static SessionManager instance;
    private final String PREFERENCE_NAME = "BABUJI_PREFERENCE";
    private final Context mContext;

    public static final String loginUser = "loginUser";
    public static final String notificationCount = "notification count";


    SharedPreferences sharedpreferences;


    private SessionManager(Context context) {
        mContext = context;
    }

    public void saveUserInfo(RegisterUser registerUser) {
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(registerUser);
        editor.putString(loginUser, json);
        editor.commit();
    }


    public RegisterUser  getUserInfo(){
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedpreferences.getString(loginUser, "");
        RegisterUser obj = gson.fromJson(json,RegisterUser.class);
        return obj;
    }


    public void saveNotificationCount(String count) {
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(notificationCount, count);
        editor.commit();
    }


    public String  getNotificationCount(){
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String lng = sharedpreferences.getString(notificationCount, "0");
        return lng;
    }



    /**
     * Method to get single instance of this class
     *
     * @return SessionManager instance of class
     */
    public static SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context);
        }
        return instance;
    }

    public  void clearData(){
        sharedpreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.clear();
        editor.commit();
    }
}
