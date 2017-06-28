package com.mycompany.readmark.api.model;


import com.mycompany.readmark.api.ApiCompleteListener;

/**
 * Created by Lenovo.
 */

public interface IBookDetailModel {

    /**
     * 获取评论
     * @param bookId
     * @param start
     * @param count
     * @param fields
     * @param listener
     */
    void loadReviewsList(String bookId, int start, int count, String fields, ApiCompleteListener listener);


    /**
     * 获取相关图书
     * @param SeriesId
     * @param start
     * @param count
     * @param fields
     * @param listener
     */
    void loadSeriesList(String SeriesId, int start, int count, String fields, ApiCompleteListener listener);

    /**
     * 取消加载数据
     */
    void cancelLoading();
}
