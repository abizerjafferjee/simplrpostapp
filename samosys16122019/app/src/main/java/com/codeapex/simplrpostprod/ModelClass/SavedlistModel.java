package com.codeapex.simplrpostprod.ModelClass;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SavedlistModel {

    @SerializedName("listId")
    @Expose
    private String listId;
    @SerializedName("listName")
    @Expose
    private String listName, isDefault;

    public SavedlistModel(String listId, String listName, String isDefault) {
        this.listId = listId;
        this.listName = listName;
        this.isDefault = isDefault;
    }

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }
}

