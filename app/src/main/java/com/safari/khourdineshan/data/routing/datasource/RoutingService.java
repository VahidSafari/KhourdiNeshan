package com.safari.khourdineshan.data.routing.datasource;

import org.neshan.servicessdk.direction.model.NeshanDirectionResult;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RoutingService {
    @GET("v4/direction")
    Call<NeshanDirectionResult> getRoute(
            @Query("type") String type,
            @Query("origin") String origin,
            @Query("destination") String destination
    );
}
