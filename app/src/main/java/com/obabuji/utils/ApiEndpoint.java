package com.obabuji.utils;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiEndpoint {


    @GET("user_registration.php")
    Call<String> signUp(@Query("name") String name, @Query("mobile") String mobile_number, @Query("device_id") String device_id,@Query("email") String email,@Query("password") String password,@Query("type") int type, @Query("profile_pic") String profile_pic);

    @GET("user_login.php")
    Call<String> userLogin(@Query("mobile") String username, @Query("password") String password, @Query("device_id") String device_id , @Query("email") String email);

    @GET("user_update_profile.php")
    Call<String> updateProfile(@Query("user_id") String user_id, @Query("name") String name, @Query("email") String email, @Query("company") String company, @Query("mobile") String mobile, @Query("isImage") int isImage);

    @Multipart
    @POST("user_update_profile.php")
    Call<String> updateProfilePost(@Query("user_id") String user_id, @Query("name") String name, @Query("email") String email, @Query("company") String company, @Query("mobile") String mobile, @Query("isImage") int isImage,@Part MultipartBody.Part image);


    @GET("paid_user_login.php")
    Call<String> paidUserLogin(@Query("user_id") String user_id,@Query("email") String email,@Query("mobile") String mobile,@Query("otp") String otp);

    @GET("get_paid_status.php")
    Call<String> checkStatus(@Query("user_id") String user_id);


    @GET("get_service_by_user.php")
    Call<String> getServices(@Query("user_id") String user_id);

    @GET("add_request_service.php")
    Call<String> requestServices(@Query("user_id") String user_id,@Query("service_name") String service_name);


    @GET("add_feedback.php")
    Call<String> addFeedback(@Query("user_id") String user_id,@Query("feedback") String feedback,@Query("helpfull") String helpfull,@Query("use") String use,@Query("design") String design);

    @GET("get_complaint.php")
    Call<String> getAllMessage(@Query("sender_id") String user_id,@Query("receiver_id") String receiver_id);



    @GET("add_complaint.php")
    Call<String> sendTextMessage(@Query("complaint_msg") String complaint_msg, @Query("sender_id") String user_id,@Query("receiver_id") String receiver_id);

    @GET("get_notification.php")
    Call<String> getNotifications();

}
