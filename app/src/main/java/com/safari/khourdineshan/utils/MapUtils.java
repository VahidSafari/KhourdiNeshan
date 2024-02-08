package com.safari.khourdineshan.utils;

import android.location.Location;

import com.safari.khourdineshan.core.mapper.LocationMapper;

import org.neshan.common.model.LatLng;
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

    public static void focusOnRectangleOfTwoPoints(MapView mapView, LatLng latLng1, LatLng latLng2) {
        double centerFirstMarkerX = latLng1.getLatitude();
        double centerFirstMarkerY = latLng1.getLongitude();
        double centerFocalPositionX = (centerFirstMarkerX + latLng2.getLatitude()) / 2;
        double centerFocalPositionY = (centerFirstMarkerY + latLng2.getLongitude()) / 2;
        mapView.moveCamera(new LatLng(centerFocalPositionX, centerFocalPositionY), 0.5f);
        mapView.setZoom(14, 0.5f);
    }

}
