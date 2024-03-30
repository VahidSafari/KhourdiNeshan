package com.safari.khourdineshan.data.routing.repository;

import com.safari.khourdineshan.core.model.base.Result;

import org.neshan.common.model.LatLng;

import io.reactivex.Observable;

public interface RoutingRepository {
    Observable<Result> getRouteResponseObservable();

    void getCarRoute(LatLng originLatLng, LatLng destinationLatLng);

    void cancelRoutingRequest();
}
