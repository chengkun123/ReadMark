package com.mycompany.readmark.bean.http;

import java.util.ArrayList;

/**
 * Created by Lenovo.
 */

public class BookSeriesListResponse extends BookListResponse {
    public BookSeriesListResponse() {
        this.books = new ArrayList<>();
    }
}