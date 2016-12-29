package com.mycompany.readmark.search;

/**
 * Created by Lenovo on 2016/11/22.
 */
public class SearchedInfoBean {
    private int id;
    private String mKeyWord;
    private String mAuthor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        mAuthor = author;
    }

    public String getKeyWord() {
        return mKeyWord;
    }

    public void setKeyWord(String keyWord) {
        mKeyWord = keyWord;
    }
}
