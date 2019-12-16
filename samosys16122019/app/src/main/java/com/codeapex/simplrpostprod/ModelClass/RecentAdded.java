package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecentAdded {


    @SerializedName("categoryId")
    @Expose
    private String categoryId;
    @SerializedName("iconURL")
    @Expose
    private String iconURL;
    @SerializedName("backgroundColor")
    @Expose
    private String backgroundColor;
    @SerializedName("categoryName")
    @Expose
    private String categoryName;

    public RecentAdded(String categoryId, String categoryName, String iconURL) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.iconURL = iconURL;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}