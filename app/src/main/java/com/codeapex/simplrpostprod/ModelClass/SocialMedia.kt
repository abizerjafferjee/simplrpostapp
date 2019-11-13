package com.codeapex.simplrpostprod.ModelClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class SocialMedia :Serializable{

    @SerializedName("website")
    @Expose
    var website: String? = null
    @SerializedName("facebook")
    @Expose
    var facebook: String? = null
    @SerializedName("twitter")
    @Expose
    var twitter: String? = null
    @SerializedName("linkedin")
    @Expose
    var linkedin: String? = null
    @SerializedName("instagram")
    @Expose
    var instagram: String? = null

}