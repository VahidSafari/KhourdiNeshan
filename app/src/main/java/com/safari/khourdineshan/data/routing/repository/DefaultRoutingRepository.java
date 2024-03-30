package com.safari.khourdineshan.data.routing.repository;

import androidx.annotation.NonNull;

import com.safari.khourdineshan.core.model.base.Result;
import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.data.routing.datasource.RoutingService;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.NeshanDirectionResult;

import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DefaultRoutingRepository implements RoutingRepository {

    private static final String ROUTING_TYPE_PARAM_CAR = "car";
    private final RoutingService routingService;
    private final BehaviorSubject<Result> carRouteResult = BehaviorSubject.create();
    private Call<NeshanDirectionResult> onTheFlightRoutingRequestCall;

    public DefaultRoutingRepository(RoutingService routingRemoteDataSource) {
        this.routingService = routingRemoteDataSource;
    }

    @Override
    public Observable<Result> getRouteResponseObservable() {
        return carRouteResult;
    }

    @Override
    public void getCarRoute(LatLng originLatLng, LatLng destinationLatLng) {
        cancelRoutingRequest();
        onTheFlightRoutingRequestCall = routingService.getRoute(ROUTING_TYPE_PARAM_CAR, LocationMapper.LatLngToString(originLatLng), LocationMapper.LatLngToString(destinationLatLng));
        onTheFlightRoutingRequestCall.enqueue(new Callback<NeshanDirectionResult>() {
            @Override
            public void onResponse(@NonNull Call<NeshanDirectionResult> call, @NonNull Response<NeshanDirectionResult> response) {
                if (response.body() != null && response.isSuccessful()) {
                    carRouteResult.onNext(new Result.Success<>(response.body().getRoutes().get(0)));
                } else {
                    try {
                        carRouteResult.onNext(new Result.Fail(new Throwable(response.errorBody() != null ? response.errorBody().string() : "request failed. try again")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<NeshanDirectionResult> call, @NonNull Throwable t) {
                carRouteResult.onNext(new Result.Fail(t));

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
