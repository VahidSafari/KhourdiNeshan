package com.safari.khourdineshan.data.routing.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.data.routing.datasource.RoutingService;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.NeshanDirectionResult;
import org.neshan.servicessdk.direction.model.Route;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefaultRoutingRepository implements RoutingRepository {

    private static final String ROUTING_TYPE_PARAM_CAR = "car";
    private final RoutingService routingService;
    private final MutableLiveData<Result<Route>> carRouteResult = new MutableLiveData<>();
    private Call<NeshanDirectionResult> onTheFlightRoutingRequestCall;

    public DefaultRoutingRepository(RoutingService routingRemoteDataSource) {
        this.routingService = routingRemoteDataSource;
    }

    @Override
    public LiveData<Result<Route>> getRouteResponseLiveData() {
        return carRouteResult;
    }

    @Override
    public void getCarRoute(LatLng originLatLng, LatLng destinationLatLng) {
        cancelRoutingRequest();
        onTheFlightRoutingRequestCall = routingService.getRoute(ROUTING_TYPE_PARAM_CAR, LocationMapper.LatLngToString(originLatLng), LocationMapper.LatLngToString(destinationLatLng));
        onTheFlightRoutingRequestCall.enqueue(new Callback<NeshanDirectionResult>() {
            @Override
            public void onResponse(Call<NeshanDirectionResult> call, Response<NeshanDirectionResult> response) {
                if (response.body() != null) {
                    carRouteResult.setValue(new Result.Success<>(response.body().getRoutes().get(0)));
                } else {
                    try {
                        carRouteResult.setValue(new Result.Fail(new Throwable(response.errorBody() != null ? response.errorBody().string() : "request failed. try again")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<NeshanDirectionResult> call, Throwable t) {
                carRouteResult.setValue(new Result.Fail(t));

            }
        });
    }

    @Override
    public void cancelRoutingRequest() {
        if (onTheFlightRoutingRequestCall != null && !onTheFlightRoutingRequestCall.isCanceled()) {
            onTheFlightRoutingRequestCall.cancel();
        }
    }
}
