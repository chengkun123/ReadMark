package com.mycompany.readmark.api.commen.service;


import com.mycompany.readmark.bean.http.BookSeriesListResponse;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Lenovo.
 */

public interface IBookSeriesService {
    @GET("book/series/{seriesId}/books")
    Observable<Response<BookSeriesListResponse>>
    getBookSeries(
            @Path("seriesId") String seriesId
            , @Query("start") int start
            , @Query("count") int count
            , @Query("fields") String fields);

}
