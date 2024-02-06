package com.safari.khourdineshan.core.mapper;

import android.location.Location;

import androidx.annotation.Nullable;

import org.neshan.common.model.LatLng;

public class LocationMapper {

    @Nullable
    public static LatLng LocationToLatLng(Location location) {
        if (location != null) {
            return new LatLng(location.getLatitude(), location.getLongitude());
        } else {
            return null;
        }
    }

}
