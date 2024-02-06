package com.safari.khourdineshan.data.routing.datasource;

import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.core.base.Result;
import com.safari.khourdineshan.model.RouteResponse;

import org.neshan.common.model.LatLng;

public interface RoutingRemoteDataSource {
    LiveData<Result<RouteResponse>> getCarRoute(LatLng originLatLng, LatLng destinationLatLng);
}
