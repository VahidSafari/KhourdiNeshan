package com.safari.khourdineshan.data.routing.repository;

import androidx.lifecycle.LiveData;

import com.safari.khourdineshan.core.ResponseState;

public interface RoutingRepository {
    LiveData<ResponseState> getRoute();
}
