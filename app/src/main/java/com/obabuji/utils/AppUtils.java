package com.obabuji.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.appcompat.BuildConfig;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.obabuji.LoginActivity;
import com.obabuji.R;
import com.obabuji.session.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppUtils {

    private static final String TAG = AppUtils.class.getSimpleName();
    private static Toast toast;

    /**
     * get device width
     *
     * @param context current context
     * @return device width
     */
    public static int getDeviceWidth(Context context) {
        return getDeviceWidthHeight(context, 0);
    }

    /**
     * Method used to get device width and height
     *
     * @param context current context
     * @param dimName int variable 0 for width and 1 for height
     * @return width/height
     */
    private static int getDeviceWidthHeight(Context context, int dimName) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        int deviceHeight = displayMetrics.heightPixels;
        if (dimName == 0) { // for width
            return deviceWidth;
        } else { // for Height
            return deviceHeight;
        }
    }

    /**
     * get device height
     *
     * @param context current context
     * @return device height
     */
    public static int getDeviceHeight(Context context) {
        return getDeviceWidthHeight(context, 1);
    }


    public static void showToast(Context context, String message) {
        try {
            if (toast != null)
                toast.cancel();
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            toast.show();
        } catch (IllegalStateException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Method used to check device is connected to internet or not
     *
     * @param mContext current context
     * @return true if internet is connected else false
     */
    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isInternetAvailable = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if (!isInternetAvailable) {
            showToast(mContext, mContext.getString(R.string.no_internet));
        }
        return isInternetAvailable;
    }

    /**
     * Hides the soft keyboard
     *
     * @param context current context
     * @param view    attached view
     */
    public static void hideSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * Shows the soft keyboard
     *
     * @param context current context
     * @param view    attached view
     */
    public static void showSoftKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        view.requestFocus();
        inputMethodManager.showSoftInput(view, 0);
    }

    /**
     * disable the double click bug this method disable the view(button) click for few milliseconds
     * so to disable the simultaneous clicks
     *
     * @param v view to be disable
     */
    public static void disableHootPlayerClick(final View v) {
        v.setEnabled(false);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 2000);
    }

    /**
     * disable the double click bug this method disable the view(button) click for few milliseconds
     * so to disable the simultaneous clicks
     *
     * @param v view to be disable
     */
    public static void disableDoubleClick(final View v) {
        v.setEnabled(false);
        v.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setEnabled(true);
            }
        }, 600);
    }

    public static String getCurrentAppVersion() {
        return BuildConfig.VERSION_NAME;
    }


    /**
     * method used to check string is null or empty
     *
     * @param s string to be checked
     * @return true if string is null or empty else false
     */
    public static boolean isNullOrEmpty(String s) {
        return (s == null) || (s.length() == 0) || (s.equalsIgnoreCase("null"));
    }

    public static void appShare(Context context,String subject,String text) {
        Intent target = new Intent();
        target.setAction(Intent.ACTION_SEND);
        target.putExtra(Intent.EXTRA_SUBJECT, subject);
        target.putExtra(Intent.EXTRA_TEXT, text);
        target.setType("text/plain");
        context.startActivity(Intent.createChooser(target, "CDGI"));
    }

    public  static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String changeDatetimeFormat(String format,String value){
        String result="";

        try {
            Date oldDate = new SimpleDateFormat(format).parse(value);
            result = new SimpleDateFormat(AppConstants.DMY).format(oldDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String changeDatetimeFormatCoupon(String format,String value){
        String result="";
        try {
            Date oldDate = new SimpleDateFormat(format).parse(value);
            result = new SimpleDateFormat("dd MMM yyyy").format(oldDate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void hideKeyboard(View view,Context ctx){

        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    public static void logout(final Context ctx){

        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle(ctx.getResources().getString(R.string.logout));
        alertDialog.setMessage(ctx.getResources().getString(R.string.logout_message));
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"yes",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SessionManager.getInstance(ctx).clearData();
                Intent intent = new Intent(ctx,LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                ctx.startActivity(intent);
                dialog.dismiss();
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"no",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alertDialog.show();

    }



    public static void alert(final Context ctx,String message){

        final AlertDialog alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Thank you!");
        alertDialog.setMessage(message);
        alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Ok",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        alertDialog.show();

    }

    public static int countOccurrences(String haystack, char needle)
    {
        int count = 0;
        for (int i=0; i < haystack.length(); i++)
        {
            if (haystack.charAt(i) == needle)
            {
                count++;
            }
        }
        return count;
    }


}