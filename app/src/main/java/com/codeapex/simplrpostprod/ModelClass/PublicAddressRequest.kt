package com.codeapex.simplrpostprod.ModelClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PublicAddressRequest :Serializable {

    @SerializedName("logoPicture")
    @Expose
    var logoPicture: String? = null
    @SerializedName("userId")
    @Expose
    var userId: String? = null
    @SerializedName("addressId")
    @Expose
    var addressId: String? = null
    @SerializedName("categoryName")
    @Expose
    var categoryName: String? = null

    @SerializedName("pictureURL")
    @Expose
    var pictureURL: String? = null
    @SerializedName("shortName")
    @Expose
    var shortName: String? = null
    @SerializedName("categoryId")
    @Expose
    var categoryId: String? = null
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
    @SerializedName("socialMedia")
    @Expose
    var socialMedia: SocialMedia? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("workingHours")
    @Expose
    var workingHours: ArrayList<WorkingHour>? = null
    @SerializedName("services")
    @Expose
    var services: ArrayList<Service>? = null
    @SerializedName("images")
    @Expose
    var images: ArrayList<Image>? = null
    @SerializedName("deliveryAvailable")
    @Expose
    var deliveryAvailable: String? = null
    @SerializedName("landmark")
    @Expose
    var landmark: String? = null
    @SerializedName("locationPictureURL")
    @Expose
    var locationPictureURL: String? = null
    @SerializedName("serviceDescription")
    @Expose
    var serviceDescription: String? = null


}