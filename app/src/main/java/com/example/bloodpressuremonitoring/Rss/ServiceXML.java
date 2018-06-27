package com.example.bloodpressuremonitoring.Rss;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface ServiceXML {

    @GET
    Call<RssFeed> getRss(@Url String url);
}
