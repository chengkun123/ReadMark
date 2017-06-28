package com.mycompany.readmark.api.commen.service;


import com.mycompany.readmark.bean.http.BookListResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lenovo.
 */

public interface IBookListService {
    //https://api.douban.com/v2/book/search?
    @GET("book/search")
    Observable<Response<BookListResponse>>
    getBookList(@Query("q") String q
            , @Query("tag") String tag
            , @Query("start") int start
            , @Query("count") int count
            , @Query("fields") String fields);


}
