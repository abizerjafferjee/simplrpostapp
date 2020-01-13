package com.codeapex.simplrpostprod.ModelClass;

public class UserData {
    String ID, addressTag,userId,profilePicURL,name,emailId,contactNumber,status;

    public UserData(String ID,String addressTag,String userId, String profilePicURL, String name, String emailId, String contactNumber, String status) {
        this.ID = ID;
        this.addressTag = addressTag;
        this.userId = userId;
        this.profilePicURL = profilePicURL;
        this.name = name;
        this.emailId = emailId;
        this.contactNumber = contactNumber;
        this.status = status;
    }


    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAddressTag() {
        return addressTag;
    }

    public void setAddressTag(String addressTag) {
        this.addressTag = addressTag;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfilePicURL() {
        return profilePicURL;
    }

    public void setProfilePicURL(String profilePicURL) {
        this.profilePicURL = profilePicURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
