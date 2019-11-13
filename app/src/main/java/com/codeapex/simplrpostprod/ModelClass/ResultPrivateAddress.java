package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultPrivateAddress {




    @SerializedName("addressId")
    @Expose
    private String addressId;
    @SerializedName("pictureURL")
    @Expose
    private String pictureURL;
    @SerializedName("shortName")
    @Expose
    private String shortName;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("plusCode")
    @Expose
    private String plusCode;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;
    @SerializedName("addressReferenceId")
    @Expose
    private String addressReferenceId;

    public Boolean getPriavte() {
        return isPriavte;
    }

    public void setPriavte(Boolean priavte) {
        isPriavte = priavte;
    }

    private  Boolean isPriavte;

    public ResultPrivateAddress(String addressId, String shortName, String plusCode, String addressReferenceId, String pictureURL, Boolean isPriavte, String categoryName, String description) {

        this.addressId = addressId;
        this.shortName = shortName;
        this.plusCode = plusCode;
        this.addressReferenceId = addressReferenceId;
        this.categoryName = categoryName;
        this.description = description;
        this.isPriavte = isPriavte;
        this.pictureURL = pictureURL;


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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

}