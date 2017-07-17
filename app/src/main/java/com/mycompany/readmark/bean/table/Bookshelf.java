package com.mycompany.readmark.bean.table;

import java.io.Serializable;

/**
 * Created by Lenovo.
 */

public class Bookshelf implements Serializable{
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String remark;
    private String createTime;
    private int color;
    private int finished;
    private float progress;
    private float waveratio;
    private float ampratio;
    private int totalpage;
    private int currentpage;
    private int red;
    private int green;
    private int blue;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getFinished() {
        return finished;
    }

    public void setFinished(int finished) {
        this.finished = finished;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public float getWaveratio() {
        return waveratio;
    }

    public void setWaveratio(float waveratio) {
        this.waveratio = waveratio;
    }

    public float getAmpratio() {
        return ampratio;
    }

    public void setAmpratio(float ampratio) {
        this.ampratio = ampratio;
    }

    public int getTotalpage() {
        return totalpage;
    }

    public void setTotalpage(int totalpage) {
        this.totalpage = totalpage;
    }

    public int getCurrentpage() {
        return currentpage;
    }

    public void setCurrentpage(int currentpage) {
        this.currentpage = currentpage;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }
}
