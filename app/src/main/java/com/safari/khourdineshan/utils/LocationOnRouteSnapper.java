package com.safari.khourdineshan.utils;

import android.location.Location;

import com.carto.core.MapPos;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;

import org.neshan.servicessdk.direction.model.DirectionStep;

import java.util.ArrayList;

public abstract class LocationOnRouteSnapper {

    public static Location snapLocationOnRoute(Location location, ArrayList<DirectionStep> directionSteps) {
        Point pt =
                new GeometryFactory().createPoint(new Coordinate(location.getX(), gpsPosition.getY()));
        DistanceOp distanceOp = new DistanceOp(pt, lineString);
        Coordinate coordinate = distanceOp.nearestPoints()[1];
        return new MapPos(coordinate.x, coordinate.y);
    }

}
