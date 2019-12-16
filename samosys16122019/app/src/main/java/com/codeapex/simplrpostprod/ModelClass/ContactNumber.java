package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ContactNumber implements Serializable {
    @SerializedName("phoneNumber")
    @Expose
    private String phoneNumber;

    public ContactNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }



    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
