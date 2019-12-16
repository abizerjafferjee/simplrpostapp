package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Service implements Serializable {
    @SerializedName("serviceId")
    @Expose
    private String serviceId;
    @SerializedName("serviceFileURL")
    @Expose
    private String serviceFileURL;
    @SerializedName("fileExtention")
    @Expose
    private String fileExtention;

    public Service(String serviceId, String serviceFileURL, String fileExtention) {
        this.serviceFileURL = serviceFileURL;
        this.fileExtention = fileExtention;
        this.serviceId = serviceId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceFileURL() {
        return serviceFileURL;
    }

    public void setServiceFileURL(String serviceFileURL) {
        this.serviceFileURL = serviceFileURL;
    }

    public String getFileExtention() {
        return fileExtention;
    }

    public void setFileExtention(String fileExtention) {
        this.fileExtention = fileExtention;
    }
}
