package com.codeapex.simplrpostprod.RetrofitApi;

import com.codeapex.simplrpostprod.ModelClass.PrivateAddressRequest;
import com.codeapex.simplrpostprod.ModelClass.PublicAddressRequest;
import com.codeapex.simplrpostprod.ModelClass.PublicImageRequest;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface Api {

    @POST(Constants.GETBASE_URL)
    Call<Object> getbaseurl();

    @POST(Constants.signIn)
    Call<Object> signIn(@Body Map<String, String> body);

    @FormUrlEncoded
    @POST(Constants.signUp)
    Call<ResponseBody> signUp(
            @Field("password") String password,
            @Field("contactNumber") String number
    );

    @POST(Constants.forgot_password)
    Call<Object> forgot_password(@Body Map<String, String> body);

    @POST(Constants.validateOTP)
    Call<Object> validate_otp(@Body Map<String, String> body);

    @POST(Constants.reset_password)
    Call<Object> reset_password(@Body Map<String, String> body);

    @POST(Constants.resend_otp)
    Call<Object> resend_otp(@Body Map<String, String> body);

    @POST(Constants.getProfile)
    Call<Object> getProfile(@Body Map<String, String> body);

    @POST(Constants.editProfile)
    Call<Object> editProfile(@Body Map<String, String> body);

    @POST(Constants.registerDevice)
    Call<Object> registerDevice(@Body Map<String, String> body);

    @POST(Constants.addSavedList)
    Call<Object> addSavedList(@Body Map<String, String> body);

    @POST(Constants.editSavedList)
    Call<Object> editSavedList(@Body Map<String, String> body);

    @POST(Constants.signOut)
    Call<Object> signOut(@Body Map<String, String> body);

    @POST(Constants.deleteAccount)
    Call<Object> deleteAccount(@Body Map<String, String> body);

    @POST(Constants.aboutUs)
    Call<Object> about_us();

    @POST(Constants.emailVerified)
    Call<Object> emailVerified(@Body Map<String, String> body);

    @POST(Constants.contactNumberVerified)
    Call<Object> resendVerificationOTP(@Body Map<String, String> body);

    @POST(Constants.privacyPolicy)
    Call<Object> privacyPolicy();

    @POST(Constants.termsConditions)
    Call<Object> termsConditions();

    @POST(Constants.deactivateAccount)
    Call<Object> deactivateAccount(@Body Map<String, String> body);

    @POST(Constants.userFeedback)
    Call<Object> userFeedback(@Body Map<String, String> body);

    @POST(Constants.changePassword)
    Call<Object> changePassword(@Body Map<String, String> body);

    @POST(Constants.getNotificationList)
    Call<Object> getNotificationList(@Body Map<String, String> body);

    @POST(Constants.validateQR)
    Call<Object> validateQR(@Body Map<String, String> body);

    @POST(Constants.addAddressPrivate)
    Call<Object> addAddressPrivate(@Body PrivateAddressRequest body);

    @POST(Constants.editPrivateAddress)
    Call<Object> editAddressPrivate(@Body PrivateAddressRequest body);

    @POST(Constants.addAddressPublic)
    Call<Object> addAddressPublic(@Body PublicAddressRequest body);

    @POST(Constants.editPublicPrimary)
    Call<Object> editPublicPrimary(@Body PublicAddressRequest body);

    @POST(Constants.editPublicLocation)
    Call<Object> editPublicLocation(@Body PublicAddressRequest body);

    @POST(Constants.editPublicServices)
    Call<Object> editPublicServices(@Body PublicAddressRequest body);

    @POST(Constants.editPublicMiscellaneous)
    Call<Object> editPublicMiscellanous(@Body PublicAddressRequest body);

    @POST(Constants.addPublicAddressImages)
    Call<Object> addAddressPublicImages(@Body PublicImageRequest body);

    @POST(Constants.editPublicAddress)
    Call<Object> editPublicAddress(@Body PublicAddressRequest body);

    @POST(Constants.editPrivatePrimary)
    Call<Object> editPrivatePrimary(@Body PrivateAddressRequest body);

    @POST(Constants.editPrivateLocation)
    Call<Object> editPrivateLocation(@Body PrivateAddressRequest body);

//    @POST(Constants.addAddressPrivate)
//    Call<Object> addAddressPrivate(@Body Example example);

//    @POST(Constants.getPrivateAddresses)
//    @FormUrlEncoded
//    Call<List<ResultPrivateAddress>> getPrivateAddresses(@Field("userId") String userId);

    @POST(Constants.getPrivateAddresses)
    Call<Object> getPrivateAddresses(@Body Map<String, String> body);

    @POST(Constants.getPublicAddresses)
    Call<Object> getPublicAddresses(@Body Map<String, String> body);

    @POST(Constants.getCategoryBusiness)
    Call<Object> getCategoryBusiness(@Body Map<String, String> body);

    @POST(Constants.getSavedList)
    Call<Object> getSavedList(@Body Map<String, String> body);

    @POST(Constants.unsaveAddressToSavedList)
    Call<Object> unsaveAddressToSavedList(@Body Map<String, String> body);

    @POST(Constants.getPrimaryCategories)
    Call<Object> getPrimaryCategories(@Body Map<String, String> body);

    @POST(Constants.getOwnPrivateAddressDetail)
    Call<Object> getOwnPrivateAddressDetail(@Body Map<String, String> body);

    @POST(Constants.getPrivateAddressDetail)
    Call<Object> getPrivateAddressDetail(@Body Map<String, String> body);

    @POST(Constants.getOwnPublicAddressDetail)
    Call<Object> getOwnPublicAddressDetail(@Body Map<String, String> body);

    @POST(Constants.getSavedListAddresses)
    Call<Object> getSavedListAddresses(@Body Map<String, String> body);

    @POST(Constants.getPublicAddressDetail)
    Call<Object> getPublicAddressDetail(@Body Map<String, String> body);

    @POST(Constants.getCategories)
    Call<Object> getCategories(@Body Map<String, String> body);

    @POST(Constants.deletePrivateAddress)
    Call<Object> deletePrivateAddress(@Body Map<String, String> body);

    @POST(Constants.deleteSavedAddressList)
    Call<Object> deleteSavedAddressList(@Body Map<String, String> body);

    @POST(Constants.deletePublicAddress)
    Call<Object> deletePublicAddress(@Body Map<String, String> body);

    @POST(Constants.saveAddressToSavedList)
    Call<Object> saveAddressToSavedList(@Body Map<String, String> body);

    @POST(Constants.searchBusiness)
    Call<Object> searchBusiness(@Body Map<String, String> body);

    @POST(Constants.addBusinessView)
    Call<Object> addBusinessView(@Body Map<String, String> body);

    @POST(Constants.getAddressIdWithReferenceCode)
    Call<Object> getAddressIdWithReferenceCode(@Body Map<String, String> body);

    @POST(Constants.validateEmailId)
    Call<Object> validateEmail(@Body Map<String, String> body);

    @POST(Constants.getUserSharedAddresses)
    Call<Object> getUserSharedAddresses(@Body Map<String, String> body);

    @POST(Constants.getBusinessSharedAddresses)
    Call<Object> getBusinessSharedAddresses(@Body Map<String, String> body);

    @POST(Constants.shareAddressWithBusiness)
    Call<Object> shareAddressWithBusiness(@Body Map<String, String> body);

    @POST(Constants.shareAddressWithUser)
    Call<Object> shareAddressWithUser(@Body Map<String, String> body);

    @POST(Constants.getFAQ)
    Call<Object> getFAQ(@Body Map<String, String> body);

    @POST(Constants.getIssues)
    Call<Object> getIssues(@Body Map<String, String> body);

    @POST(Constants.submitReport)
    Call<Object> submitReport(@Body Map<String, String> body);

    @POST(Constants.getReceipientList)
    Call<Object> getReceipientList(@Body Map<String, String> body);

    @POST(Constants.unshareAddressWithBusiness)
    Call<Object> unshareAddressWithBusiness(@Body Map<String, String> body);

    @POST(Constants.unshareAddressWithUser)
    Call<Object> unshareAddressWithUser(@Body Map<String, String> body);

    @Multipart
    @POST(Constants.addPublicAddressService)
    Call<Object> addPublicAddressService(
            @Part MultipartBody.Part filePart,
            @Part("addressId") RequestBody addressId,
            @Part("serviceId") RequestBody serviceId
    );
}