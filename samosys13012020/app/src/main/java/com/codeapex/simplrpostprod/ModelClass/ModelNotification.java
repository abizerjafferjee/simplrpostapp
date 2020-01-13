package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelNotification {
    @SerializedName("notificationId")
    @Expose
    private String notificationId;
    @SerializedName("notificationInformation")
    @Expose
    private String notificationInformation;
    @SerializedName("createDate")
    @Expose
    private String createDate;

    public ModelNotification(String notificationId, String notificationInformation, String createDate) {
        this.notificationId = notificationId;
        this.notificationInformation = notificationInformation;
        this.createDate = createDate;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationInformation() {
        return notificationInformation;
    }

    public void setNotificationInformation(String notificationInformation) {
        this.notificationInformation = notificationInformation;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
