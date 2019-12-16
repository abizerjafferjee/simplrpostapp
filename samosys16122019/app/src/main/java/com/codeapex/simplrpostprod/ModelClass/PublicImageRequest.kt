package com.codeapex.simplrpostprod.ModelClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PublicImageRequest:Serializable {

    @SerializedName("image")
    @Expose
    var image: String? = null
    @SerializedName("addressId")
    @Expose
    var addressId: String? = null
    @SerializedName("imageId")
    @Expose
    var imageId: String? = "-1"

}