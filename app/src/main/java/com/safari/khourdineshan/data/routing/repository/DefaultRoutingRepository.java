package com.safari.khourdineshan.data.routing.repository;

import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.core.ResponseState;
import com.safari.khourdineshan.data.routing.datasource.RoutingService;

public class DefaultRoutingRepository implements RoutingRepository {
    private RoutingService routingService;

    @Override
    public LiveData<ResponseState> getRoute() {
        
    }
}
