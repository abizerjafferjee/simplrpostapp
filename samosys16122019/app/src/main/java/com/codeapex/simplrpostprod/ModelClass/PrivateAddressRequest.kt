package com.codeapex.simplrpostprod.ModelClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class PrivateAddressRequest:Serializable {

    @SerializedName("addressPicture")
    @Expose
    var addressPicture: String? = null
    @SerializedName("pictureURL")
    @Expose
    var pictureURL: String? = null
    @SerializedName("addressId")
    @Expose
    var addressId: String? = null
    @SerializedName("userId")
    @Expose
    var userId: String? = null
    @SerializedName("shortName")
    @Expose
    var shortName: String? = null
    @SerializedName("latitude")
    @Expose
    var latitude: String? = null
    @SerializedName("longitude")
    @Expose
    var longitude: String? = null
    @SerializedName("address")
    @Expose
    var address: String? = null
    @SerializedName("contactNumber")
    @Expose
    var contactNumber: List<ContactNumber>? = null
    @SerializedName("emailId")
    @Expose
    var emailId: String? = null
    @SerializedName("landmark")
    @Expose
    var landmark: String? = null


}

