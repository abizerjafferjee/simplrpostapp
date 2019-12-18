package com.codeapex.simplrpostprod.UtilityClass.Api_utils;

/*
  Created by SOHEL KHAN on 09-Mar-18.
 */

import com.codeapex.simplrpostprod.RetrofitApi.Constants;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("User/signIn")
    Call<ResponseBody> login(
            @Field("contactNumber") String contactNumber,
            @Field("password") String password,
            @Field("login_type") String login_type
    );

    @FormUrlEncoded
    @POST(Constants.signUp)
    Call<ResponseBody> signUp(
            @Field("contactNumber") String number,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST(Constants.registerDevice)
    Call<ResponseBody> registerDevice(@Field(Constants.userId) String userId,
                                      @Field(Constants.deviceId) String deviceId,
                                      @Field(Constants.deviceType) String deviceType,
                                      @Field(Constants.pushToken) String pushToken);

    @POST(Constants.registerDevice)
    Call<Object> registerDevice_old(@Body Map<String, String> body);

    @FormUrlEncoded
    @POST("User/verify_otp")
    Call<ResponseBody> validate_otp(@Field(Constants.otpId) String userId,
                                    @Field("otp") String deviceId,
                                    @Field("type") String otpType);
    @FormUrlEncoded
    @POST("User/verify_email_otp")
    Call<ResponseBody> validate_otp_email_edit(@Field(Constants.otpId) String userId,
                                    @Field("otp") String deviceId,
                                               @Field("type") String otpType);
    @FormUrlEncoded
    @POST("User/resend_otp")
    Call<ResponseBody> resend_otp(@Field(Constants.contactNumber) String contactNumber,
                                  @Field("type") String otpType);

    @FormUrlEncoded
    @POST("User/resend_email_otp")
    Call<ResponseBody> resend_otp_for_email_varification(@Field(Constants.userId) String contactNumber,
                                                         @Field("type") String otpType);


    @FormUrlEncoded
    @POST("User/get_security_question")
    Call<ResponseBody> getQuestion(@Field(Constants.userId) String contactNumber);


    @FormUrlEncoded
    @POST("User/forgot_password_email")
    Call<ResponseBody> forgot_password(@Field(Constants.email_id) String email_id);

    @FormUrlEncoded
    @POST("User/forgot_password_phone")
    Call<ResponseBody> forgot_password_second(@Field(Constants.contactNumber) String contactNumber);


    @FormUrlEncoded
    @POST("User/verify_security_answer")
    Call<ResponseBody> submit_security_question(@Field(Constants.userId) String userId,@Field("security_answer") String security_answer);

    @FormUrlEncoded
    @POST("User/change_password")
    Call<ResponseBody> GeneratePassword(@Field(Constants.userId) String userId,
                                        @Field("password") String password);

    @POST("forgot_password_contact")
    Call<Object> forgot_password_contact(@Body Map<String, String> body);

    @Multipart
    @POST("User/editProfile")
    Call<ResponseBody> updateProfileMultipart(
            @Part(Constants.userId) RequestBody UserId,
            @Part(Constants.contactno) RequestBody FcmId,
            @Part(Constants.email_id) RequestBody Fullname,
            @Part("security_question") RequestBody Email,
            @Part("security_answer") RequestBody Phone,
            @Part(Constants.namee) RequestBody Dob,
            @Part MultipartBody.Part image
    );

    @FormUrlEncoded
    @POST("User/editProfile")
    Call<ResponseBody> updateProfile(
            @Field(Constants.userId) String UserId,
            @Field(Constants.contactno) String FcmId,
            @Field(Constants.email_id) String Fullname,
            @Field("security_question") String Email,
            @Field("security_answer") String Phone,
            @Field(Constants.namee) String Dob,
            @Field(Constants.profilePicURL) String profilePicURL

    );

}