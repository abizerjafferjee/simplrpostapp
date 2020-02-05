package com.codeapex.simplrpostprod.UtilityClass.Api_utils;

/*
  Created by SOHEL KHAN on 09-Mar-18.
 */

import com.codeapex.simplrpostprod.RetrofitApi.Constants;
import com.fenchtose.tooltip.Tooltip;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
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

    @FormUrlEncoded
    @POST("User/verifyEmailId")
    Call<ResponseBody> verifyOTPProfile(
            @Field("userId") String userId,
            @Field("emailId") String emailId
    );

    @Multipart
    @POST("User/editProfile")
    Call<ResponseBody> updateProfileMultipart(
            @Part(Constants.userId) RequestBody UserId,
            @Part(Constants.contactno) RequestBody FcmId,
            @Part(Constants.email_id) RequestBody Fullname,
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

    @Multipart
    @POST(Constants.addAddressPrivate)
    Call<ResponseBody> savePrivateAddress(
            @Part(Constants.userId) RequestBody UserId,
            @Part("address_tag") RequestBody address_tag,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("plus_code") RequestBody plus_code,
            @Part("unique_link") RequestBody unique_link,
            @Part("country") RequestBody country,
            @Part("city") RequestBody city,
            @Part("street_name") RequestBody street_name,
            @Part("building_name") RequestBody building_name,
            @Part("entrance_name") RequestBody entrance_name,
            @Part("direction_text") RequestBody direction_text,
            @Part("street_img_type") RequestBody street_img_type,
            @Part("building_img_type") RequestBody building_img_type,
            @Part("entrance_img_type") RequestBody entrance_img_type,
            @Part MultipartBody.Part street_image,
            @Part MultipartBody.Part building_image,
            @Part MultipartBody.Part entrance_image
    );

    @Multipart
    @POST(Constants.addAddressPublic)
    Call<ResponseBody> savePublicAddress(
            @Part(Constants.userId) RequestBody UserId,
            @Part("address_tag") RequestBody address_tag,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("plus_code") RequestBody plus_code,
            @Part("unique_link") RequestBody unique_link,
            @Part("country") RequestBody country,
            @Part("city") RequestBody city,
            @Part("street_name") RequestBody street_name,
            @Part("building_name") RequestBody building_name,
            @Part("entrance_name") RequestBody entrance_name,
            @Part("direction_text") RequestBody direction_text,
            @Part("street_img_type") RequestBody street_img_type,
            @Part("building_img_type") RequestBody building_img_type,
            @Part("entrance_img_type") RequestBody entrance_img_type,
            @Part MultipartBody.Part street_image,
            @Part MultipartBody.Part building_image,
            @Part MultipartBody.Part entrance_image
    );

    @FormUrlEncoded
    @POST("address/updateAddress")
    Call<ResponseBody> editPublicPrivateAddress(
            @Field("userId") String UserId,
            @Field("addressId") String addressId,
            @Field("type") String type,
            @Field("address_tag") String address_tag,
            @Field("latitude") String latitude,
            @Field("longitude") String longitude,
            @Field("plus_code") String plus_code,
            @Field("unique_link") String unique_link,
            @Field("country") String country,
            @Field("city") String city,
            @Field("street_name") String street_name,
            @Field("building_name") String building_name,
            @Field("entrance_name") String entrance_name,

            @Field("direction_text") String direction_text,
            @Field("street_img_type") RequestBody street_img_type,
            @Field("building_img_type") RequestBody building_img_type,
            @Field("entrance_img_type") RequestBody entrance_img_type,
            @Field("street_image") String street_image,
            @Field ("building_image") String building_image,
            @Field ("entrance_image") String entrance_image
    );

    @Multipart
    @POST("address/updateAddress")
    Call<ResponseBody> editPublicPrivateAddressWithImage(
            @Part("userId") RequestBody UserId,
            @Part("addressId") RequestBody addressId,
            @Part("type") RequestBody type,
            @Part("address_tag") RequestBody address_tag,
            @Part("latitude") RequestBody latitude,
            @Part("longitude") RequestBody longitude,
            @Part("plus_code") RequestBody plus_code,
            @Part("unique_link") RequestBody unique_link,
            @Part("country") RequestBody country,
            @Part("city") RequestBody city,
            @Part("street_name") RequestBody street_name,
            @Part("building_name") RequestBody building_name,
            @Part("entrance_name") RequestBody entrance_name,
            @Part("street_img_remove") RequestBody street_remove,
            @Part("building_img_remove") RequestBody building_remove,
            @Part("entrance_img_remove") RequestBody entrance_remove,
            @Part("direction_text") RequestBody direction_text,
            @Part("street_img_type") RequestBody street_img_type,
            @Part("building_img_type") RequestBody building_img_type,
            @Part("entrance_img_type") RequestBody entrance_img_type,
            @Part MultipartBody.Part street_image,
            @Part MultipartBody.Part building_image,
            @Part MultipartBody.Part entrance_image
    );

    @FormUrlEncoded
    @POST("user/favoriteAddress")
    Call<ResponseBody> saveOtherUserAddress(@Field(Constants.userId)String userId,
                                            @Field(Constants.addressId)String addressId,
                                            @Field("type")String type);

    @FormUrlEncoded
    @POST("user/favoriteAddressList")
    Call<ResponseBody> getSavedAddress(@Field(Constants.userId)String userId,
                                            @Field("type")String type);

    @FormUrlEncoded
    @POST("user/unFavoriteAddress")
    Call<ResponseBody> removeSavedAddress(@Field("fav_id")String fav_id);


    @FormUrlEncoded
    @POST(Constants.getPrivateAddresses)
    Call<ResponseBody> getPrivateAddress(@Field(Constants.userId)String userId);

    @FormUrlEncoded
    @POST(Constants.getPublicAddresses)
    Call<ResponseBody> getPublicAddresses(@Field(Constants.userId)String userId);

    @FormUrlEncoded
    @POST("address/getAddressDetail")
    Call<ResponseBody> getAddress(@Field("type")String type,
                                  @Field(Constants.addressId)String addressId);
    @FormUrlEncoded
    @POST("address/getAddressLink")
    Call<ResponseBody> getuniqueLinkAddress(@Field("unique_link")String type);

    @FormUrlEncoded
    @POST(Constants.deletePrivateAddress)
    Call<ResponseBody> deletePrivateAddress(@Field(Constants.userId)String userId,
                                      @Field(Constants.addressId)String addressId);

    @FormUrlEncoded
    @POST(Constants.deletePublicAddress)
    Call<ResponseBody> deletePublicAddress(@Field(Constants.userId)String userId,
                                            @Field(Constants.addressId)String addressId);


    @GET("api")
    Call<ResponseBody> getPositionByZip(@Query("address") String address);

    @GET("json")
    Call<ResponseBody> getLocationFromAddress(@Query("input") String input,
                                              @Query("inputtype") String inputtype,
                                              @Query("fields") String fields,
                                              @Query("key") String key);

    @GET("json")
    Call<ResponseBody> getADDFromPlaceId(@Query("place_id") String place_id,
                                         @Query("fields") String fields,
                                         @Query("key") String key);


    @GET("json")
    Call<ResponseBody> getAddressFromLocation(@Query("latlng") String location,
                                         @Query("key") String key);

    @GET("json")
    Call<ResponseBody> getAddressFromAddress(@Query("address") String location,
                                              @Query("key") String key);

    @GET("user/users")
    Call<ResponseBody> getAppUser();

    @FormUrlEncoded
    @POST("address/getAddressLink")
    Call<ResponseBody> getAddressesByLink(@Field("unique_link")String unique_link);

    @FormUrlEncoded
    @POST(Constants.getUserSharedAddresses)
    Call<ResponseBody> getUserSharedAddresses(@Field(Constants.userId)String userId);

    @FormUrlEncoded
    @POST("user/privateGlobalSearch")
    Call<ResponseBody> searchPrivateAddresses(@Field("search")String search);

    @FormUrlEncoded
    @POST("user/publicGlobalSearch")
    Call<ResponseBody> searchPublicAddresses(@Field("search")String search);

    @FormUrlEncoded
    @POST("user/getPrivateSharedRecieverList")
    Call<ResponseBody> getPrivateRecievedAddressList(@Field(Constants.userId)String userId);

    @FormUrlEncoded
    @POST("user/getPublicSharedRecieverList")
    Call<ResponseBody> getPublicRecievedAddressList(@Field(Constants.userId)String userId);

    @FormUrlEncoded
    @POST("user/getShareRecieverUserList")
    Call<ResponseBody> getPublicSharedSenderList(@Field(Constants.userId)String userId,
                                                 @Field(Constants.addressId)String addressId,
                                                 @Field("type")String type);
    @FormUrlEncoded
    @POST("user/getShareRecieverUserList")
    Call<ResponseBody> getPrivateSharedSenderList(@Field(Constants.userId)String userId,
                                                  @Field(Constants.addressId)String addressId,
                                                  @Field("type")String type);

    @FormUrlEncoded
    @POST("user/unShareAddress")
    Call<ResponseBody> unShareAddress(@Field("recordId")String recordId,
                                      @Field("type")String type);


}