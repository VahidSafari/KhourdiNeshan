package com.safari.khourdineshan.data.routing.datasource;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.safari.khourdineshan.core.base.Result;
import com.safari.khourdineshan.model.RouteResponse;

import org.neshan.common.model.LatLng;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RoutingRemoteDataSourceImpl implements RoutingRemoteDataSource {

    private final RoutingService routingService;
    private Call<Response<Result<RouteResponse>>> onTheFlightRoutingRequestCall;
    private static final String ROUTING_TYPE_CAR = "car";

    public RoutingRemoteDataSourceImpl(RoutingService routingService) {
        this.routingService = routingService;
    }

    @Override
    public LiveData<Result<RouteResponse>> getCarRoute(LatLng originLatLng, LatLng destinationLatLng) {
        MutableLiveData<Result<RouteResponse>> routeResponse = new MutableLiveData<>();
        if (onTheFlightRoutingRequestCall != null && !onTheFlightRoutingRequestCall.isCanceled()) {
            onTheFlightRoutingRequestCall.cancel();
        }
        onTheFlightRoutingRequestCall = routingService.getRoute(ROUTING_TYPE_CAR, "", "");
        onTheFlightRoutingRequestCall.enqueue(new Callback<Response<Result<RouteResponse>>>() {
            @Override
            public void onResponse(@NonNull Call<Response<Result<RouteResponse>>> call, @NonNull Response<Response<Result<RouteResponse>>> response) {
                if (response.body() != null && response.body().body() != null) {
                    routeResponse.setValue(response.body().body());
                } else {
                    routeResponse.setValue(new Result.Fail(new Throwable("invalid response")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response<Result<RouteResponse>>> call, @NonNull Throwable t) {
                routeResponse.setValue(new Result.Fail(t));
            }
        });
        return routeResponse;
    }

}
