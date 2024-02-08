package com.safari.khourdineshan.data.routing.datasource;

import com.safari.khourdineshan.core.base.Result;

import org.neshan.servicessdk.direction.model.Route;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RoutingService {
    @GET("v4/direction")
    Call<Response<Result<Route>>> getRoute(
            @Query("type") String type,
            @Query("origin") String origin,
            @Query("destination") String destination
    );
}
