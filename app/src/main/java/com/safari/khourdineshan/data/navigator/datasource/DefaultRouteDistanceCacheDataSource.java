package com.safari.khourdineshan.data.navigator.datasource;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toRadians;

import androidx.annotation.NonNull;

import com.safari.khourdineshan.utils.LocationConverters;

import org.neshan.common.model.LatLng;
import org.neshan.common.utils.PolylineEncoding;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.Route;

import java.util.List;
import java.util.Map;

public class DefaultRouteDistanceCacheDataSource implements RouteDistanceCacheDataSource {

    private List<DirectionStep> directionSteps;
    private Map<Integer, List<Double>> stepToDistancesMap;

    @Override
    public void setDirectionSteps(@NonNull Route directionSteps) {
        if (directionSteps.equals(this.directionSteps)) {
            // no update is necessary!
        } else {
            this.directionSteps = directionSteps;
            generateDistances();
        }
    }

    private void generateDistances() {
        for (DirectionStep directionStep : directionSteps) {
            List<LatLng> directionStepLatLngs = PolylineEncoding.decode(directionStep.getEncodedPolyline());
            for (int i = 0; i < directionStepLatLngs.size() - 1; i++) {
                calculateDistance(directionStepLatLngs.get(i), directionStepLatLngs.get(i + 1));
            }
        }
    }

    private static final double EARTH_RADIUS_KM = 6371.0;
    public static double calculateDistance(LatLng latLng1, LatLng latLng2) {
        // Convert degrees to radians
        double lat1Rad = toRadians(latLng1.getLatitude());
        double lon1Rad = toRadians(latLng1.getLongitude());
        double lat2Rad = toRadians(latLng2.getLatitude());
        double lon2Rad = toRadians(latLng2.getLongitude());

        // Calculate differences
        double deltaLat = lat2Rad - lat1Rad;
        double deltaLon = lon2Rad - lon1Rad;

        // Calculate distance using Haversine formula
        double haversineTerm = pow(sin(deltaLat / 2), 2) + cos(lat1Rad) * cos(lat2Rad) * pow(sin(deltaLon / 2), 2);
        double c = 2 * atan2(sqrt(haversineTerm), sqrt(1 - haversineTerm));

        return EARTH_RADIUS_KM * c;
    }

    @Override
    public Double getStepToDistancesMap() {
        if (stepToDistancesMap != null) {
            return null;
        } else {
            return null;
        }
    }
}
