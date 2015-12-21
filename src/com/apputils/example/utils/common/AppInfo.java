package com.apputils.example.utils.common;

import android.graphics.drawable.Drawable;

public class AppInfo {
    private String name = "";
    
    private String packag = "";
    
    private String versionName = "";
    
    private Drawable icon;
    
    private int version = 0;
    
    private String path = "";
    
    private String image;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPackage() {
        return packag;
    }
    
    public void setPackage(String packag) {
        this.packag = packag;
    }
    
    public String getVersionName() {
        return versionName;
    }
    
    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }
    
    public Drawable getIcon() {
        return icon;
    }
    
    public void setIcon(Drawable drawable) {
        this.icon = drawable;
    }
    
    public int getVersion() {
        return version;
    }
    
    public void setVersion(int version) {
        this.version = version;
    }
    
    public String getPath() {
        return path;
    }
    
    public void setPath(String path) {
        this.path = path;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
}
