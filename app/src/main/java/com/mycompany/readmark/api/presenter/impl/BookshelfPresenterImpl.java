package com.mycompany.readmark.api.presenter.impl;

import com.mycompany.readmark.api.ApiCompleteListener;
import com.mycompany.readmark.api.model.IBookshelfModel;
import com.mycompany.readmark.api.model.impl.BookshelfModelImpl;
import com.mycompany.readmark.api.presenter.IBookshelfPresenter;
import com.mycompany.readmark.api.view.IBookListView;
import com.mycompany.readmark.bean.http.BaseResponse;
import com.mycompany.readmark.bean.table.Bookshelf;

import java.util.List;

/**
 * Created by Lenovo.
 */

public class BookshelfPresenterImpl implements IBookshelfPresenter, ApiCompleteListener {
    private IBookshelfModel mBookshelfModel;
    private IBookListView mBookListView;

    public BookshelfPresenterImpl(IBookListView bookListView) {
        mBookListView = bookListView;
        mBookshelfModel = new BookshelfModelImpl();
    }

    /**
     * 加载书签
     */
    @Override
    public void loadBookshelf() {
        mBookshelfModel.loadBookshelf(this);
        mBookListView.showProgress();
    }

    /**
     * 添加一本书到数据库
     * @param bookshelf
     */
    @Override
    public void addBookshelf(Bookshelf bookshelf) {
        mBookListView.showProgress();
        mBookshelfModel.addBookshelf(bookshelf, this);
    }

    /**
     * 更新数据库里的一本书
     * @param bookshelf bookshelf
     */
    @Override
    public void updateBookshelf(Bookshelf bookshelf) {
        mBookListView.showProgress();
        mBookshelfModel.updateBookshelf(bookshelf, this);
    }

    /**
     * 将数据库中的书排序
     * @param id     id
     * @param front  前一个bookshelf order
     * @param behind 后一个bookshelf order
     */
    @Override
    public void orderBookshelf(int id, long front, long behind) {
        mBookListView.showProgress();
        mBookshelfModel.orderBookshelf(id, front, behind, this);
    }

    /**
     * 删除数据库中的一本书
     * @param id id
     */
    @Override
    public void deleteBookshelf(String id) {
        mBookListView.showProgress();
        mBookshelfModel.deleteBookshelf(id, this);
    }

    @Override
    public void unSubscribe() {
        mBookshelfModel.unSubscribe();
    }

    /**
     * 数据库操作成功回调presenter，同时刷新view
     *
     * @param result
     */
    @Override
    public void onCompleted(Object result) {
        if(result instanceof List){
            mBookListView.refreshData(result);
        }else if(result instanceof BaseResponse){
            mBookListView.showMessage(((BaseResponse)result).getCode() + "|" + ((BaseResponse)result).getMsg());
        }else if(result instanceof String){
            mBookListView.showMessage((String) result);
        }
        mBookListView.hideProgress();
    }

    /**
     * 数据库操作失败回调presenter
     * @param msg
     */
    @Override
    public void onFailed(BaseResponse msg) {
        mBookListView.hideProgress();
        if(msg != null){
            mBookListView.showMessage(msg.getCode() + "|" +msg.getMsg());
        }
    }
}
