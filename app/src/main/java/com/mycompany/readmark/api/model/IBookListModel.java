package com.mycompany.readmark.api.model;


import com.mycompany.readmark.api.ApiCompleteListener;

/**
 * Created by Lenovo.
 */

public interface IBookListModel {

    void loadBookList(String q, String tag, int start, int count, String fields, ApiCompleteListener listener);

    void cancelLoading();

}
