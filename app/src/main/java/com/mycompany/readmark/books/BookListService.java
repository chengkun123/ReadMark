package com.mycompany.readmark.books;

import java.util.Map;

import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Lenovo on 2016/11/8.
 */
public interface BookListService {
    @GET("book/search")
    Observable<BookListBean> getBookList(@QueryMap Map<String, String> options);
}
