package com.codeapex.simplrpostprod.ModelClass;

import android.graphics.drawable.Drawable;

public class shareItemsModel {
    public String app = "";
    public Drawable icon;
    public String packageName;

    public shareItemsModel(String name, Drawable drawable,String packageName) {
        this.app = name;
        this.icon = drawable;
        this.packageName = packageName;
    }
}
