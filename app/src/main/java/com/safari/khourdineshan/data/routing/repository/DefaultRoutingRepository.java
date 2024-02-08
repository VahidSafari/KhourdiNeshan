package com.safari.khourdineshan.data.routing.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.safari.khourdineshan.core.base.Result;
import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.data.routing.datasource.RoutingService;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.Route;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefaultRoutingRepository implements RoutingRepository {

    private static final String ROUTING_TYPE_PARAM_CAR = "car";
    private final RoutingService routingService;
    private final MutableLiveData<Result<Route>> carRouteResult = new MutableLiveData<>();
    private Call<Response<Result<Route>>> onTheFlightRoutingRequestCall;

    public DefaultRoutingRepository(RoutingService routingRemoteDataSource) {
        this.routingService = routingRemoteDataSource;
    }

    @Override
    public LiveData<Result<Route>> getRouteResponseLiveData() {
        return carRouteResult;
    }

    @Override
    public void getCarRoute(LatLng originLatLng, LatLng destinationLatLng) {
        if (onTheFlightRoutingRequestCall != null && !onTheFlightRoutingRequestCall.isCanceled()) {
            onTheFlightRoutingRequestCall.cancel();
        }
        onTheFlightRoutingRequestCall = routingService.getRoute(ROUTING_TYPE_PARAM_CAR, LocationMapper.LatLngToString(originLatLng), LocationMapper.LatLngToString(destinationLatLng));
        onTheFlightRoutingRequestCall.enqueue(new Callback<Response<Result<Route>>>() {
            @Override
            public void onResponse(@NonNull Call<Response<Result<Route>>> call, @NonNull Response<Response<Result<Route>>> response) {
                if (response.body() != null && response.body().body() != null) {
                    carRouteResult.setValue(response.body().body());
                } else {
                    carRouteResult.setValue(new Result.Fail(new Throwable("invalid response")));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Response<Result<Route>>> call, @NonNull Throwable t) {
                carRouteResult.setValue(new Result.Fail(t));
            }
        });
    }
}
