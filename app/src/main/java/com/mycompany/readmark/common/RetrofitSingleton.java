package com.mycompany.readmark.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mycompany.readmark.books.BookListService;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitSingleton {
    public static final String BASE_URL = "https://api.douban.com/v2/";
    private BookListService service;


    static class RetrofitHolder{
        private static RetrofitSingleton retrofitSingleton = new RetrofitSingleton();
    }


    public static RetrofitSingleton getRetrofit(){
        return RetrofitHolder.retrofitSingleton;
    }

    private RetrofitSingleton(){
        Executor executor = Executors.newCachedThreadPool();

        Gson gson = new GsonBuilder().create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .callbackExecutor(executor)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = retrofit.create(BookListService.class);
    }

    public BookListService getService(){
        return service;
    }
}
