package com.safari.khourdineshan.data.routing.repository;

import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.core.base.Result;
import com.safari.khourdineshan.data.routing.datasource.RoutingRemoteDataSource;
import com.safari.khourdineshan.model.RouteResponse;

import org.neshan.common.model.LatLng;

public class DefaultRoutingRepository implements RoutingRepository {
    private final RoutingRemoteDataSource routingRemoteDataSource;

    public DefaultRoutingRepository(RoutingRemoteDataSource routingRemoteDataSource) {
        this.routingRemoteDataSource = routingRemoteDataSource;
    }

    @Override
    public LiveData<Result<RouteResponse>> getCarRoute(LatLng originLatLng, LatLng destinationLatLng) {
        return routingRemoteDataSource.getCarRoute(originLatLng, destinationLatLng);
    }
}
