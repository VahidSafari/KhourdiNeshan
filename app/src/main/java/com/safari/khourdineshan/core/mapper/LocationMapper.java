package com.safari.khourdineshan.core.mapper;

import android.location.Location;

import androidx.annotation.NonNull;
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

    @NonNull
    public static String LatLngToString(LatLng latLng) {
        StringBuilder locationStringBuilder = new StringBuilder();
        if (latLng != null) {
            locationStringBuilder
                    .append(latLng.getLatitude())
                    .append(",")
                    .append(latLng.getLongitude());
        }
        return locationStringBuilder.toString();
    }

}
