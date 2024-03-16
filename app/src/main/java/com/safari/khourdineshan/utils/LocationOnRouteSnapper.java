package com.safari.khourdineshan.utils;

import android.location.Location;
import android.util.Pair;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import org.neshan.common.utils.PolylineEncoding;
import org.neshan.servicessdk.direction.model.DirectionStep;

public abstract class LocationOnRouteSnapper {

    public static Pair<Location, DistanceOp> snapLocationOnStep(Location location, DirectionStep step) {
        Point pt = new GeometryFactory().createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));
        LineString stepLineString = new GeometryFactory().createLineString(LocationConverters.getCoordinatesFromLatLng(PolylineEncoding.decode(step.getEncodedPolyline())));
        DistanceOp distanceOp = new DistanceOp(pt, stepLineString);
        Coordinate coordinate = distanceOp.nearestPoints()[1];
        Location snappedLocation = new Location("jts");
        snappedLocation.setLatitude(coordinate.y);
        snappedLocation.setLongitude(coordinate.x);
        return new Pair<>(snappedLocation, distanceOp);
    }

}
