package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Image implements Serializable {


    @SerializedName("imageId")
    @Expose
    private String imageId;
    @SerializedName("imageURL")
    @Expose
    private String imageURL;

    public Image(String imageId, String imageURL) {
        this.imageId = imageId;
        this.imageURL = imageURL;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}

