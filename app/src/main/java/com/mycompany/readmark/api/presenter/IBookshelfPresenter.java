package com.mycompany.readmark.api.presenter;


import com.mycompany.readmark.api.ApiCompleteListener;
import com.mycompany.readmark.bean.table.Bookshelf;

/**
 * Created by Lenovo.
 */
public interface IBookshelfPresenter {
    /**
     * 获取我的书架
     */
    void loadBookshelf();

    /**
     * 添加一个书架
     * @param bookshelf
     */
    void addBookshelf(Bookshelf bookshelf);

    /**
     * 修改一个书架
     *
     * @param bookshelf bookshelf
     */
    void updateBookshelf(Bookshelf bookshelf);

    /**
     * 排序
     *
     * @param id     id
     * @param front  前一个bookshelf order
     * @param behind 后一个bookshelf order
     */
    void orderBookshelf(int id, long front, long behind);

    /**
     * 清空书架
     *
     * @param id id
     */
    void deleteBookshelf(String id);

    /**
     * 取消订阅
     */
    void unSubscribe();
}
