package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BussinessModel {


    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("addressId")
    @Expose
    private String addressId;

    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("profilePicURL")
    @Expose
    private String profilePicURL;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("name")
    @Expose
    private String name;

    private Boolean isUser;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(String plusCode) {
        this.plusCode = plusCode;
    }

    private String category;

    private String plusCode;

    public BussinessModel(String userId, String profilePicURL, String userName, String name,  Boolean isUser,String addressId,String shortName, String category, String plusCode){
        this.userId = userId;
        this.profilePicURL = profilePicURL;
        this.userName = userName;
        this.name = name;
        this.isUser = isUser;
        this.addressId = addressId;
        this.shortName = shortName;
        this.category = category;
        this.plusCode = plusCode;
    }



    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public Boolean getUser() {
        return isUser;
    }

    public void setUser(Boolean user) {
        isUser = user;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
