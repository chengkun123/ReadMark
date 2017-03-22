package com.mycompany.readmark.marker;

import java.io.Serializable;

/**
 * Created by Lenovo on 2017/3/18.
 */
public class MarkerBean implements Serializable{
    private int id;
    private String markerName;
    private String imageUrl;
    private float progress;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMarkerName() {
        return markerName;
    }

    public void setMarkerName(String markerName) {
        this.markerName = markerName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
