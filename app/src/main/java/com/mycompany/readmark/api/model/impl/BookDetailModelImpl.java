package com.mycompany.readmark.api.model.impl;


import com.mycompany.readmark.api.ApiCompleteListener;
import com.mycompany.readmark.api.commen.ServiceFactory;
import com.mycompany.readmark.api.commen.service.IBookReviewsService;
import com.mycompany.readmark.api.commen.service.IBookSeriesService;
import com.mycompany.readmark.api.model.IBookDetailModel;
import com.mycompany.readmark.bean.http.BaseResponse;
import com.mycompany.readmark.bean.http.BookReviewsListResponse;
import com.mycompany.readmark.bean.http.BookSeriesListResponse;
import com.mycompany.readmark.commen.URL;

import java.net.UnknownHostException;

import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Lenovo.
 */

public class BookDetailModelImpl implements IBookDetailModel {

    @Override
    public void loadReviewsList(String bookId, int start, int count, String fields,final ApiCompleteListener listener) {
        IBookReviewsService iBookReviewsService = ServiceFactory
                .createService(URL.HOST_URL_DOUBAN, IBookReviewsService.class);
        iBookReviewsService.getBookReviews(bookId, start, count, fields)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<BookReviewsListResponse>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if(e instanceof UnknownHostException){
                            listener.onFailed(null);
                        }
                        listener.onFailed(new BaseResponse(404, e.getMessage()));
                    }

                    @Override
                    public void onNext(Response<BookReviewsListResponse> bookReviewsListResponseResponse) {
                        if(bookReviewsListResponseResponse.isSuccessful()){
                            listener.onCompleted(bookReviewsListResponseResponse.body());
                        }else{
                            listener.onFailed(new BaseResponse(bookReviewsListResponseResponse.code()
                                    , bookReviewsListResponseResponse.message()));
                        }
                    }
                });

    }

    @Override
    public void loadSeriesList(String SeriesId, int start, int count, String fields,final ApiCompleteListener listener) {
        IBookSeriesService iBookSeriesService = ServiceFactory.createService(URL.HOST_URL_DOUBAN, IBookSeriesService.class);
        iBookSeriesService.getBookSeries(SeriesId, start, count, fields)
                .subscribeOn(Schedulers.newThread())    //请求在新的线程中执行
                .observeOn(AndroidSchedulers.mainThread())//最后在主线程中执行
                .subscribe(new Subscriber<Response<BookSeriesListResponse>>() {
                    @Override
                    public void onCompleted() {

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
                    public void onNext(Response<BookSeriesListResponse> bookSeriesResponse) {
                        if (bookSeriesResponse.isSuccessful()) {
                            listener.onCompleted(bookSeriesResponse.body());
                        } else {
                            listener.onFailed(new BaseResponse(bookSeriesResponse.code(), bookSeriesResponse.message()));
                        }

                    }
                });
    }

    @Override
    public void cancelLoading() {

    }
}
