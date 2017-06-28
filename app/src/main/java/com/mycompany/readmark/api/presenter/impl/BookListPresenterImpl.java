package com.mycompany.readmark.api.presenter.impl;


import com.mycompany.readmark.BaseApplication;
import com.mycompany.readmark.R;
import com.mycompany.readmark.api.ApiCompleteListener;
import com.mycompany.readmark.api.model.IBookListModel;
import com.mycompany.readmark.api.model.impl.BookListModelImpl;
import com.mycompany.readmark.api.presenter.IBookListPresenter;
import com.mycompany.readmark.api.view.IBookListView;
import com.mycompany.readmark.bean.http.BaseResponse;
import com.mycompany.readmark.bean.http.BookListResponse;
import com.mycompany.readmark.utils.commen.NetworkUtils;

/**
 * Created by Lenovo.
 */

public class BookListPresenterImpl implements IBookListPresenter, ApiCompleteListener {

    private IBookListView mIBookListView;
    private IBookListModel mIBookListModel;

    public BookListPresenterImpl(IBookListView IBookListView) {
        mIBookListView = IBookListView;
        mIBookListModel = new BookListModelImpl();
    }

    @Override
    public void loadBooks(String q, String tag, int start, int count, String fields) {
        if(!NetworkUtils.isConnected(BaseApplication.getApplication())){
            mIBookListView.showMessage(BaseApplication.getApplication().getString(R.string.poor_network));
            mIBookListView.hideProgress();
        }
        mIBookListView.showProgress();
        mIBookListModel.loadBookList(q, tag, start, count, fields, this);
    }


    @Override
    public void cancelLoading() {
        mIBookListModel.cancelLoading();
    }

    @Override
    public void onCompleted(Object result) {
        if(result instanceof BookListResponse){
            int index = ((BookListResponse)result).getStart();
            if(index == 0){
                mIBookListView.refreshData(result);
            }else{
                mIBookListView.addData(result);
            }
            mIBookListView.hideProgress();
        }
    }

    @Override
    public void onFailed(BaseResponse msg) {
        mIBookListView.hideProgress();
        if(msg == null){
            return;
        }
        mIBookListView.showMessage(msg.getMsg());
    }
}
