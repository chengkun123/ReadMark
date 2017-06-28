package com.mycompany.readmark.api.presenter;

/**
 * Created by Lenovo.
 */

public interface IBookDetailPresenter {
    /**
     * 获取评价
     * @param bookId
     * @param start
     * @param count
     * @param fields
     */
    void loadReviews(String bookId, int start, int count, String fields);


    /**相关数目推荐
     * @param bookId
     * @param start
     * @param count
     * @param fields
     */
    void loadSeries(String bookId, int start, int count, String fields);

    /**
     * 取消加载
     */
    void cancelLoading();

}
