package com.codeapex.simplrpostprod.ModelClass

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


class WorkingHour constructor(val day: String, val day_Id: String): Serializable {
    @SerializedName("dayId")
    @Expose
    var dayId: String? = null
    @SerializedName("dayName")
    @Expose
    var dayName: String? = null

    init {
        dayName = day
        dayId = day_Id
    }

    @SerializedName("isOpen")
    @Expose
    var isOpen: String? = "0"
    @SerializedName("openTime")
    @Expose
    var openTime: String? = null
    @SerializedName("closeTime")
    @Expose
    var closeTime: String? = null

}