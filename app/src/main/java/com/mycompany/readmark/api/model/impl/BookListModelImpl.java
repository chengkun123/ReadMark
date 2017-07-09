package com.mycompany.readmark.api.model.impl;



import android.util.Log;

import com.mycompany.readmark.api.ApiCompleteListener;
import com.mycompany.readmark.api.commen.ServiceFactory;
import com.mycompany.readmark.api.commen.service.IBookListService;
import com.mycompany.readmark.api.model.IBookListModel;
import com.mycompany.readmark.bean.http.BaseResponse;
import com.mycompany.readmark.bean.http.BookListResponse;
import com.mycompany.readmark.commen.URL;

import java.net.UnknownHostException;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lenovo.
 */

public class BookListModelImpl implements IBookListModel {

    @Override
    public void loadBookList(String q, String tag, int start, int count, String fields
            , final ApiCompleteListener listener) {
        IBookListService iBookListService = ServiceFactory.createService(URL.HOST_URL_DOUBAN, IBookListService.class);
        iBookListService.getBookList(q, tag, start, count, fields)
                .subscribeOn(Schedulers.io())    //请求在io线程中执行
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<Response<BookListResponse>>() {
                    @Override
                    public void onCompleted() {
                        listener.onCompleted("");
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof UnknownHostException) {
                            listener.onFailed(null);
                            return;
                        }
                        listener.onFailed(new BaseResponse(404, e.getMessage()));
                    }

                    @Override
                    public void onNext(Response<BookListResponse> bookListResponse) {
                        if (bookListResponse.isSuccessful()) {
                            listener.onCompleted(bookListResponse.body());
                        } else {
                            listener.onFailed(new BaseResponse(bookListResponse.code(), bookListResponse.message()));
                        }

                    }
                });
    }

    @Override
    public void cancelLoading() {

    }
}
