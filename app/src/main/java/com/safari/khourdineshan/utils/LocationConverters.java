package com.safari.khourdineshan.utils;

import android.location.Location;

import androidx.annotation.Nullable;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.neshan.common.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationConverters {

    @Nullable
    public static Location getCoordinateFromLatLng(LatLng latLng) {
        if (latLng != null) {
            Location location = new Location("converter");
            location.setLongitude(latLng.getLongitude());
            location.setLatitude(latLng.getLatitude());
            return location;
        } else {
            return null;
        }
    }

    public static Coordinate[] getCoordinatesFromLatLng(List<LatLng> latLngList) {
        Coordinate[] coordinates = new Coordinate[latLngList.size()];
        for (int i = 0; i < latLngList.size(); i++) {
            coordinates[i] = new Coordinate(latLngList.get(i).getLongitude(), latLngList.get(i).getLatitude());
        }
        return coordinates;
    }

    public static Point LocationToPoint(Location location) {
        return new GeometryFactory().createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));
    }

    public static Location latLngToLocation(LatLng latLng) {
        Location location = new Location("location provider");
        location.setLatitude(latLng.getLatitude());
        location.setLongitude(latLng.getLongitude());
        return location;
    }

}
