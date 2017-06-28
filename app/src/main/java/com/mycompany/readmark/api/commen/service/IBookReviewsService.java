package com.mycompany.readmark.api.commen.service;


import com.mycompany.readmark.bean.http.BookReviewsListResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lenovo.
 */

public interface IBookReviewsService {
    @GET("book/{bookId}/reviews")
    Observable<Response<BookReviewsListResponse>>
    getBookReviews(@Path("bookId") String bookId
            , @Query("start") int start
            , @Query("count") int count
            , @Query("fields") String fields);

}