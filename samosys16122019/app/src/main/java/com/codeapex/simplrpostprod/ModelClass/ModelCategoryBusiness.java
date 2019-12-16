package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelCategoryBusiness {


    @SerializedName("addressId")
    @Expose
    private String addressId;
    @SerializedName("pictureURL")
    @Expose
    private String pictureURL;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("plusCode")
    @Expose
    private String plusCode;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("addressReferenceId")
    @Expose
    private String addressReferenceId;
    @SerializedName("description")
    @Expose
    private String description;

    public ModelCategoryBusiness(String addressId, String pictureURL, String shortName, String plusCode, String categoryName, String addressReferenceId, String description) {

        this.pictureURL = pictureURL;
        this.categoryName = categoryName;
        this.addressReferenceId = addressReferenceId;

        this.addressId = addressId;
        this.shortName = shortName;
        this.description = description;
        this.plusCode = plusCode;



    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public void setPictureURL(String pictureURL) {
        this.pictureURL = pictureURL;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getPlusCode() {
        return plusCode;
    }

    public void setPlusCode(String plusCode) {
        this.plusCode = plusCode;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getAddressReferenceId() {
        return addressReferenceId;
    }

    public void setAddressReferenceId(String addressReferenceId) {
        this.addressReferenceId = addressReferenceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
