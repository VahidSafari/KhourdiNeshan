package com.safari.khourdineshan.ui;

import com.carto.styles.MarkerStyle;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.MapView;
import org.neshan.mapsdk.model.Marker;

public class CurrentLocationMarker {

    private Marker marker;
    private final MarkerStyle markerStyle;

    public CurrentLocationMarker(MarkerStyle markerStyle) {
        this.markerStyle = markerStyle;
    }

    public void updateLatLng(LatLng latLng, MapView mapView) {
        if (marker == null) {
            marker = new Marker(latLng, markerStyle);
            mapView.addMarker(marker);
        } else {
            marker.setLatLng(latLng);
        }
    }

    public LatLng getLatLng() {
        return marker.getLatLng();
    }

}
