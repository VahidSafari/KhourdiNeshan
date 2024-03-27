package com.safari.khourdineshan.utils;

import android.location.Location;

import com.carto.core.ScreenBounds;
import com.carto.core.ScreenPos;
import com.safari.khourdineshan.core.mapper.LocationMapper;

import org.neshan.common.model.LatLng;
import org.neshan.common.model.LatLngBounds;
import org.neshan.mapsdk.MapView;

import java.util.List;

public class MapUtils {

    public static void focusOnLocation(MapView mapView, Location location) {
        mapView.moveCamera(LocationMapper.LocationToLatLng(location), 0.5f);
        mapView.setZoom(15, 0.5f);
    }

    public static LatLngBounds FoundFitBound(List<LatLng> latLngs) {
        double minLng = Double.MAX_VALUE;
        double minLat = Double.MAX_VALUE;
        double maxLng = Double.MIN_VALUE;
        double maxLat = Double.MIN_VALUE;

        for (LatLng latLng : latLngs) {
            minLng = Math.min(minLng, latLng.getLongitude());
            minLat = Math.min(minLat, latLng.getLatitude());
            maxLng = Math.max(maxLng, latLng.getLongitude());
            maxLat = Math.max(maxLat, latLng.getLatitude());
        }
        return new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
    }

    public static void focusOnRoute(MapView mapView, List<LatLng> latLngs) {

        mapView.setTilt(90, 0);
        mapView.setRotationX(0);
        mapView.setRotationY(0);

        int width = mapView.getWidth();
        int height = mapView.getHeight();
        int marginFromRectangleToMapView = width / 10;
        ScreenBounds screenBounds = new ScreenBounds(
                new ScreenPos(marginFromRectangleToMapView, marginFromRectangleToMapView),
                new ScreenPos(width - marginFromRectangleToMapView, height - marginFromRectangleToMapView)
        );

        LatLngBounds fitBound = FoundFitBound(latLngs);
        mapView.moveToCameraBounds(fitBound, screenBounds, false, 0.5f);
    }

}
