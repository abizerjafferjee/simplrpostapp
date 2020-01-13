package com.codeapex.simplrpostprod.ModelClass;

public class ShareModel {

    String userId, userImage, userName, mobileNumber;

    public ShareModel(String userId, String userImage, String userName, String mobileNumber) {
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.mobileNumber = mobileNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
