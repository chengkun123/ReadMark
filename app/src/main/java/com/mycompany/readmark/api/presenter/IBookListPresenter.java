package com.mycompany.readmark.api.presenter;

/**
 * Created by Lenovo.
 */

public interface IBookListPresenter {
    void loadBooks(String q, String tag, int start, int count, String fields);

    void cancelLoading();
}
