package com.safari.khourdineshan.ui.mapper;

import android.location.Location;

import org.neshan.common.model.LatLng;

public class LocationMapper {

    public static LatLng LocationToLatLng(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }

}
