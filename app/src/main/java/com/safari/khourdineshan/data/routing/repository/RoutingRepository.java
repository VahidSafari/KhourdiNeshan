package com.safari.khourdineshan.data.routing.repository;

import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.core.base.Result;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.Route;

public interface RoutingRepository {
    LiveData<Result<Route>> getRouteResponseLiveData();

    void getCarRoute(LatLng originLatLng, LatLng destinationLatLng);

    void cancelRoutingRequest();
}
