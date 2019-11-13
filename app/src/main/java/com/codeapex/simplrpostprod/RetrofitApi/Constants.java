package com.codeapex.simplrpostprod.RetrofitApi;

public class Constants {


//---------------------------------------------------URLS-----------------------------------------------------------//
public static String MAIN_URL ="http://www.admin.simplrpost.com/index.php/Api/";
    public static String BASE_URL ="http://www.admin.simplrpost.com/" ;

    public static String IMG_URL ="http://www.admin.simplrpost.com/uploads/" ;
    public static final String RESULT_DATA ="resultData" ;
    public static final String RESULTDATA ="resultData" ;
    public static final String strNetworkError ="Network unavailable. Please check your network settings." ;
    public static final String someissue ="Some issue has occurred. Please try again later." ;
    public static final String someissuetitle ="Error" ;
    public static final String strAlertTitle ="Network error" ;


    //---------------------------------------------------KEYS-----------------------------------------------------------

    public static final String emailId = "emailUserName";
    public static final String password = "password";
    public static final String userName = "userName";
    public static final String name ="name";
    public static final String email_id ="emailId";
    public static final String searchText ="searchText";
    public static final String distance ="distance";
    public static final String categoryId ="categoryId";
    public static final String start ="start";
    public static final String count ="count";
    public static final String emailId1 ="emailId";

    public static final String contactNumber ="contactNumber";
    public static final String profilePicURL ="profilePicURL";
    public static final String userId ="userId";
    public static final String reporterUserId ="reporterUserId";
    public static final String reporterName ="reporterName";
    public static final String reporterEmailId ="reporterEmailId";
    public static final String referenceNumber ="referenceNumber";
    public static final String reporterContactNumber ="reporterContactNumber";
    public static final String businessId ="businessId";
    public static final String listName ="listName";
    public static final String addressId ="addressId";
    public static final String isAddressPublic ="isAddressPublic";
    public static final String isPublic ="isPublic";
    public static final String receiverId ="receiverId";
    public static final String listId ="listId";
    public static final String newPassword ="newPassword";
    public static final String currentPassword ="currentPassword";
    public static final String deviceId ="deviceId";
    public static final String deviceType ="deviceType";
    public static final String addressReferenceId = "addressReferenceId";
    public static final String pushToken ="pushToken";
    public static final String latitude ="latitude";
    public static final String longitude ="longitude";
    public static final String currentLatitude ="currentLatitude";
    public static final String currentLongitude ="currentLongitude";
    public static final String questionId ="questionId";
    public static final String issueId ="issueId";
    public static final String description ="description";
    public static final String question ="question";
    public static final String issue ="issue";
    public static final String answer ="answer";


    public static final String status ="status";
    public static final String emailforget ="emailId";
    public static final String contactno ="contactNumber";
    public static final String namee ="name";
    public static final String userNamee ="userName";
    public static final String profilePicURLL ="profilePicURL";
    public static final String isEmailUsed ="isEmailUsed";
    public static final String new_password ="password";
    public static final String otpId ="otpId";
    public static String otp_id = "otpId";
    public static String otp = "otp";

    public static String noInternetMessage = "No working internet connection found. Please check your network settings.";






    //======================Server Url===========////////
    public static final String GETBASE_URL ="user/getBaseURL" ;
    public static final String forgot_password ="user/forgotPassword" ;
    public static final String editProfile ="user/editProfile" ;
    public static final String validateOTP ="user/validateOTP" ;
    public static final String reset_password ="user/resetPassword" ;
    public static final String resend_otp ="user/resendOTP" ;
    public static final String getProfile ="user/getProfile" ;
    public static final String BASE_URL1 ="apiBaseURL" ;
    public static final String IMAGE_URL1 ="imageBaseURL" ;
    public static final String signIn ="user/signIn" ;
    public static final String signUp ="User/signUp" ;
    public static final String isUserLoggedIn ="isUserLoggedIn" ;
    public static final String baseURLKey ="baseURL" ;
    public static final String imageBaseURLKey ="imageBaseURL" ;
    public static final String registerDevice ="user/registerDevice" ;
    public static final String signOut ="user/signOut" ;
    public static final String deleteAccount ="user/deleteAccount" ;
    public static final String deactivateAccount ="user/deactivateAccount" ;
    public static final  String validateEmailId = "user/validateEmailId";
    public static final  String emailVerified = "user/resendVerificationEmail";
    public static final String contactNumberVerified = "user/resendVerificationOTP";



    public static final String userFeedback ="user/userFeedback" ;
    public static final String getUserSharedAddresses ="address/getUserSharedAddresses" ;
    public static final String getBusinessSharedAddresses ="address/getBusinessSharedAddresses" ;
    public static final String shareAddressWithBusiness ="address/shareAddressWithBusiness" ;
    public static final String shareAddressWithUser ="address/shareAddressWithUser" ;
    public static final String getFAQ ="user/getFAQ" ;
    public static final String getIssues ="user/getIssues" ;
    public static final String submitReport ="user/submitReport" ;
    public static final String getReceipientList ="user/getReceipientList" ;
    public static final String unshareAddressWithBusiness ="address/unshareAddressWithBusiness" ;
    public static final String unshareAddressWithUser ="address/unshareAddressWithUser" ;

    public static final String changePassword ="user/changePassword" ;
    public static final String aboutUs ="user/aboutUs" ;
    public static final String privacyPolicy ="user/privacyPolicy" ;
    public static final String getNotificationList ="user/getNotificationList" ;
    public static final String termsConditions ="user/termsConditions" ;
    public static final String addAddressPrivate ="address/addAddressPrivate" ;
    public static final String addSavedList ="address/addSavedList" ;
    public static final String editSavedList ="address/editSavedList" ;
    public static final String addAddressPublic ="address/addAddressPublic" ;
    public static final String editPublicPrimary ="address/updatePublicAddressPrimaryInformation" ;
    public static final String editPublicLocation ="address/updatePublicAddressLocationInformation" ;
    public static final String editPublicServices ="address/updatePublicAddressServices" ;
    public static final String editPublicMiscellaneous ="address/updatePublicAddressMiscellaneousInformation" ;

    public static final String editPrivatePrimary ="address/updatePrivateAddressPrimaryInformation" ;

    public static final String editPrivateLocation ="address/updatePrivateAddressLocationInformation" ;


    public static final String validateQR ="address/validateQR" ;
    public static final String editPrivateAddress ="address/editPrivateAddress" ;
    public static final String editPublicAddress ="address/editPublicAddress" ;

    public static final String getPrivateAddresses ="address/getPrivateAddresses" ;
    public static final String getPublicAddresses ="address/getPublicAddresses" ;
    public static final String getCategoryBusiness ="address/getCategoryBusiness" ;
    public static final String getSavedList ="address/getSavedList" ;
    public static final String unsaveAddressToSavedList ="address/unsaveAddressToSavedList" ;
    public static final String getTrendingCategories ="address/getTrendingCategories" ;
    public static final String getPrimaryCategories ="address/getPrimaryCategories" ;
    public static final String getTrendingBusinesses ="address/getTrendingBusinesses" ;
    public static final String getOwnPrivateAddressDetail ="address/getOwnPrivateAddressDetail" ;
    public static final String getPrivateAddressDetail ="address/getPrivateAddressDetail" ;
    public static final String getOwnPublicAddressDetail ="address/getOwnPublicAddressDetail" ;
    public static final String getSavedListAddresses ="address/getSavedListAddresses" ;
    public static final String getPublicAddressDetail ="address/getPublicAddressDetail" ;
    public static final String getCategories ="address/getCategories" ;
    public static final String deletePrivateAddress ="address/deletePrivateAddress" ;
    public static final String deleteSavedAddressList ="address/deleteSavedAddressList" ;
    public static final String deletePublicAddress ="address/deletePublicAddress" ;
    public static final String saveAddressToSavedList ="address/saveAddressToSavedList" ;
    public static final String searchBusiness ="address/searchBusiness" ;
    public static final String addBusinessView ="address/addBusinessView" ;
    public static final String addPublicAddressService ="address/addBusinessServices" ;
    public static final String addPublicAddressImages="address/addBusinessImages" ;
    public static final String getAddressIdWithReferenceCode ="user/getAddressIdWithReferenceCode" ;

    //---------------------------------------------------RESULT_CODE-----------------------------------------------------------
    public static final String RESULT_CODE = "resultCode";
    public static final String RESULTCODE = "resultCode";

    public static final String RESULT_CODE_ONE ="1.0";
    public static final String ZERO ="0.0";


    public static String RESULT_CODE_MINUS_ONE = "-1.0";
    public static String RESULT_CODE_MINUS_THREE = "-3.0";
    public static String RESULT_CODE_MINUS_FOUR = "-4.0";
    public static String RESULT_CODE_MINUS_FIVE = "-5.0";
    public static String RESULT_CODE_MINUS_TWO = "-2.0";
    public static String RESULT_CODE_MINUS_SEVEN = "-7.0";
    public static String RESULT_CODE_MINUS_EIGHT = "-8.0";
    public static String RESULT_CODE_MINUS_SIX = "-6.0";

}
