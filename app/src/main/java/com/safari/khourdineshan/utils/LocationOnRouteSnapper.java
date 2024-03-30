package com.safari.khourdineshan.utils;

import android.location.Location;
import android.util.Pair;

import androidx.annotation.Nullable;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import org.neshan.common.utils.PolylineEncoding;
import org.neshan.servicessdk.direction.model.DirectionStep;

import java.util.List;

public abstract class LocationOnRouteSnapper {

    @Nullable
    public static SnappedLocationModel snapLocationOnRoute(Location location, List<DirectionStep> steps) {
        Coordinate bestSnappedCoordinate = null;
        Double bestSnappedCoordinateDistanceToRoute = null;
        int bestSnappedLocationStepIndex = 0;
        if (location != null) {
            for (int i = 0; i < steps.size(); i++) {
                DirectionStep directionStep = steps.get(i);
                LineString stepLineString = new GeometryFactory().createLineString(LocationConverters.getCoordinatesFromLatLng(PolylineEncoding.decode(directionStep.getEncodedPolyline())));
                DistanceOp distanceOp = new DistanceOp(LocationConverters.LocationToPoint(location), stepLineString);
                Coordinate coordinate = distanceOp.nearestPoints()[1];
                if (bestSnappedCoordinateDistanceToRoute == null || bestSnappedCoordinateDistanceToRoute > distanceOp.distance()) {
                    bestSnappedCoordinate = coordinate;
                    bestSnappedCoordinateDistanceToRoute = distanceOp.distance();
                    bestSnappedLocationStepIndex = i;
                } else {
                    // previous snapped location was nearer to the route
                }
            }
        }
        if (bestSnappedCoordinate != null) {
            Location snappedLocation = new Location("navigator");
            snappedLocation.setLatitude(bestSnappedCoordinate.y);
            snappedLocation.setLongitude(bestSnappedCoordinate.x);
            return new SnappedLocationModel(snappedLocation, bestSnappedLocationStepIndex);
        } else {
            return null;
        }
    }

    public static class SnappedLocationModel {
        private final Location location;
        private final int stepIndex;

        public SnappedLocationModel(Location location, int stepIndex) {
            this.location = location;
            this.stepIndex = stepIndex;
        }

        public Location getLocation() {
            return location;
        }

        public int getStepIndex() {
            return stepIndex;
        }
    }

}
