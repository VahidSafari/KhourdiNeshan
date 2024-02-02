package com.safari.khourdineshan.utils;

import android.location.Location;

import com.safari.khourdineshan.ui.mapper.LocationMapper;

import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;

public class MapUtils {

    public static void updateMarkerLocation(MapView mapView, Marker marker, Location location) {
        if (marker != null) {
            marker.setLatLng(LocationMapper.LocationToLatLng(location));
        }
        mapView.addMarker(marker);
    }

    public static void focusOnLocation(MapView mapView, Location location) {
        mapView.moveCamera(LocationMapper.LocationToLatLng(location), 0.5f);
        mapView.setZoom(15, 0.5f);
    }

}
