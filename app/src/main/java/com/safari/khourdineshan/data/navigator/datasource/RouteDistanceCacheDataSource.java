package com.safari.khourdineshan.data.navigator.datasource;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.neshan.servicessdk.direction.model.Route;

public interface RouteDistanceCacheDataSource {
    void setDirectionSteps(@NonNull Route directionSteps);
    @Nullable
    Double getStepToDistancesMap();
}
