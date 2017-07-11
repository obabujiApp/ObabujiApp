package com.obabuji;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.obabuji.app.MyApplication;
import com.obabuji.model.RegisterUser;
import com.obabuji.session.SessionManager;
import com.obabuji.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private EditText et_name,et_company_name,et_mobile,et_email;
    private TextView btn_edit_name,btn_edit_company,btn_mobile,btn_edit_email;
    private Context ctx;
    private CircleImageView profile_image;
    private Button btn_edit_save;
    private MultipartBody.Part partImage = null;
    private String imagePath;
    private static  final  int RESULT_LOAD_IMG = 145;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE=325;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        et_name = (EditText) findViewById(R.id.et_name);
        et_company_name = (EditText) findViewById(R.id.et_company_name);
        et_mobile = (EditText) findViewById(R.id.et_mobile);
        et_email = (EditText) findViewById(R.id.et_email);
        btn_edit_name = (TextView) findViewById(R.id.btn_edit_name);
        btn_edit_company = (TextView) findViewById(R.id.btn_edit_company);
        btn_mobile = (TextView) findViewById(R.id.btn_mobile);
        btn_edit_email = (TextView) findViewById(R.id.btn_edit_email);
        btn_edit_save = (Button) findViewById(R.id.btn_edit_save);

        ctx = EditProfileActivity.this;

        ActionBar actionBar = getSupportActionBar();

        if (actionBar!=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getResources().getString(R.string.profile));
        }

        btn_edit_name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEditName();
            }
        });

        btn_edit_company.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEditCompany();
            }
        });

        btn_mobile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEditMobile();
            }
        });

        btn_edit_email.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onEditEmail();
            }
        });

        btn_edit_save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String name = et_name.getText().toString().trim();
                String company = et_company_name.getText().toString().trim();
                String mobile = et_mobile.getText().toString().trim();
                String email = et_email.getText().toString().trim();

                if (name.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter name");
                }else if (mobile.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter mobile");
                }else if (mobile.length()<10){
                    AppUtils.showToast(ctx,"Invalid mobile number");
                }else if (email.equalsIgnoreCase("")){
                    AppUtils.showToast(ctx,"Please enter email");
                }else if (!AppUtils.isValidEmail(email)){
                    AppUtils.showToast(ctx,"Invalid email id");
                }else {
                    onUpdate(name,company,mobile,email);
                }
            }
        });


        profile_image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(checkPermission()) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
                }
            }
        });

        showDetail();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_STORAGE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
            }
        }

    }

    private boolean checkPermission() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        } else {

            // Here, thisActivity is the current activity
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
                return false;
            } else {
                return true;
            }
        }

    }


    /**
     * create multipart for profile pic
     *
     * @param f1 file object
     * @return profile MultipartBody data
     */
    private MultipartBody.Part createTestPicData(File f1) {
        RequestBody requestFile1 = RequestBody.create(MediaType.parse("image/*"), f1);
        return MultipartBody.Part.createFormData("profile_pic", f1.getName(), requestFile1);
    }

    public void onUpdate(String name,String company, String mobile, String email){

        if (!AppUtils.isNetworkAvailable(ctx)) {
            return;
        }

        if (MyApplication.getInstance().getApiEndpoint() == null)
            return;

        final ProgressDialog progressDialog = new ProgressDialog(ctx);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        progressDialog.show();

//        if(fcmID.equalsIgnoreCase("")){
//            fcmID = FirebaseInstanceId.getInstance().getToken();
//        }

        Call<String> loginCall=null;
       final String userId= SessionManager.getInstance(ctx).getUserInfo().getId();
        if(imagePath!=null) {
            partImage=null;
            partImage = createTestPicData(new File(imagePath));
            loginCall = MyApplication.getInstance().getApiEndpoint().updateProfilePost(userId,name,email,company,mobile,1,partImage);
        }else{
            loginCall = MyApplication.getInstance().getApiEndpoint().updateProfile(userId,name,email,company,mobile,0);

        }

        loginCall.enqueue(new Callback<String>() {

            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                progressDialog.hide();

                try {

                    if (response.isSuccessful() && response.body() != null) {

                        parseResponse(response.body().toString(),userId);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {

            try {
                final Uri imageUri = data.getData();
                //file:///storage/emulated/0/WhatsApp/Media/WhatsApp%20Images/IMG-20170630-WA0002.jpg
                Log.e("disdish","imageUri "+imageUri.getPath());
                imagePath = imageUri.getPath();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profile_image.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(EditProfileActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }




    public void showDetail(){
        RegisterUser registerUser = SessionManager.getInstance(ctx).getUserInfo();

        et_name.setText(registerUser.getName());
        et_email.setText(registerUser.getEmail());
        et_mobile.setText(registerUser.getPhone());
        et_company_name.setText(registerUser.getCompany());
        if (!registerUser.getProfile_pic().equalsIgnoreCase("")) {
            Glide.with(ctx).load(registerUser.getProfile_pic()).into(profile_image);
        }

    }

    private void parseResponse(String response,String id){

        try {
            JSONObject jsonObject = new JSONObject(response);

          //"user_details":[{"name":"satish","mob":"8817766123","email":"satish@obabuji.com","password":"123","profile_pic":"http:\/\/innovativeadssolution.com\/obabuji.com\/profile_pic\/IMG_20170630_093627.jpg","company":"Obabuji. com"}]}

            if(jsonObject.getString("status").equalsIgnoreCase("1")){

                JSONArray jsonArray = jsonObject.getJSONArray("user_details");

                JSONObject user_details = jsonArray.getJSONObject(0);

                RegisterUser registerUser = new RegisterUser();
                registerUser.setId(id);
                registerUser.setName(user_details.getString("name"));
                registerUser.setPhone(user_details.getString("mob"));
                registerUser.setEmail(user_details.getString("email"));
                registerUser.setProfile_pic(user_details.getString("profile_pic"));
                registerUser.setCompany(user_details.getString("company"));
                SessionManager.getInstance(ctx).saveUserInfo(registerUser);
                AppUtils.showToast(ctx, "Profile update successful.");
                finish();

            }else{
                AppUtils.showToast(ctx, jsonObject.getString("message"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onEditName(){
        et_name.setEnabled(true);
        et_name.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_name, InputMethodManager.SHOW_IMPLICIT);
     }

    public void onEditEmail(){
        et_email.setEnabled(true);
        et_email.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_email, InputMethodManager.SHOW_IMPLICIT);
    }

    public void onEditMobile(){

        et_mobile.setEnabled(true);
        et_mobile.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_mobile, InputMethodManager.SHOW_IMPLICIT);

    }

    public void onEditCompany(){

        et_company_name.setEnabled(true);
        et_company_name.requestFocus();

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_company_name, InputMethodManager.SHOW_IMPLICIT);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
