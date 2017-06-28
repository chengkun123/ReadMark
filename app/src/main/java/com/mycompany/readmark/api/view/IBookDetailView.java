package com.mycompany.readmark.api.view;

/**
 * Created by Lenovo.
 */

public interface IBookDetailView {

    void showMessage(String msg);

    void showProgress();

    void hideProgress();

    void updateView(Object result);
}
