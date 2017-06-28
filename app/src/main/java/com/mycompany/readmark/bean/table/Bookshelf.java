package com.mycompany.readmark.bean.table;

/**
 * Created by Lenovo.
 */

public class Bookshelf {
    private int index;
    private int id;
    private int bookCount;
    private String title;
    private String remark;
    private String createTime;
    private long order;
    //private int progress;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookCount() {
        return bookCount;
    }

    public void setBookCount(int bookCount) {
        this.bookCount = bookCount;
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

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    /*public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }*/
}
