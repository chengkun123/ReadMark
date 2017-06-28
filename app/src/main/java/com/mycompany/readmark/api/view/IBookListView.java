package com.mycompany.readmark.api.view;

/**
 * Created by Lenovo.
 */

public interface IBookListView {
    void showMessage(String msg);

    void showProgress();

    void hideProgress();

    void refreshData(Object result);

    void addData(Object result);

}
