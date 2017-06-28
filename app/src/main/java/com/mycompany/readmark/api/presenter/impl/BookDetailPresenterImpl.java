package com.mycompany.readmark.api.presenter.impl;


import com.mycompany.readmark.R;
import com.mycompany.readmark.api.ApiCompleteListener;
import com.mycompany.readmark.api.model.IBookDetailModel;
import com.mycompany.readmark.api.model.impl.BookDetailModelImpl;
import com.mycompany.readmark.api.presenter.IBookDetailPresenter;
import com.mycompany.readmark.api.view.IBookDetailView;
import com.mycompany.readmark.bean.http.BaseResponse;
import com.mycompany.readmark.utils.commen.NetworkUtils;
import com.mycompany.readmark.utils.commen.UIUtils;

/**
 * Created by Lenovo.
 */

public class BookDetailPresenterImpl implements IBookDetailPresenter, ApiCompleteListener {
    private IBookDetailModel mBookDetailModel;
    private IBookDetailView mBookDetailView;


    public BookDetailPresenterImpl(IBookDetailView view) {
        mBookDetailView = view;
        mBookDetailModel = new BookDetailModelImpl();
    }

    @Override
    public void loadReviews(String bookId, int start, int count, String fields) {
        if(!NetworkUtils.isConnected(UIUtils.getContext())){
            mBookDetailView.showMessage(UIUtils.getContext().getString(R.string.poor_network));
            mBookDetailView.hideProgress();
        }
        mBookDetailView.showProgress();
        mBookDetailModel.loadReviewsList(bookId, start, count, fields, this);
    }

    @Override
    public void loadSeries(String SeriesId, int start, int count, String fields) {
        if (!NetworkUtils.isConnected(UIUtils.getContext())) {
            mBookDetailView.showMessage(UIUtils.getContext().getString(R.string.poor_network));

        }
        mBookDetailModel.loadSeriesList(SeriesId, start, count, fields, this);

    }

    @Override
    public void cancelLoading() {
        mBookDetailModel.cancelLoading();
    }

    @Override
    public void onCompleted(Object result) {
        mBookDetailView.updateView(result);
        mBookDetailView.hideProgress();
    }

    @Override
    public void onFailed(BaseResponse msg) {
        mBookDetailView.hideProgress();
        if(msg == null){
            return;
        }
        mBookDetailView.showMessage(msg.getMsg());
    }
}
