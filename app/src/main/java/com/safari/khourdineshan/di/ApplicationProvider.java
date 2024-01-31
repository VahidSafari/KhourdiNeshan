package com.safari.khourdineshan.di;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.safari.khourdineshan.data.datasource.LocationDataSource;
import com.safari.khourdineshan.data.datasource.LocationDataSourceImpl;
import com.safari.khourdineshan.data.repository.DefaultLocationRepository;
import com.safari.khourdineshan.data.repository.LocationRepository;

public class ApplicationProvider {

    private static final int LOCATION_REQUEST_INTERVAL_IN_MILLIS = 1000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL_IN_MILLIS = 500;

    private static ApplicationProvider INSTANCE;
    private final Context applicationContext;
    private LocationRepository defaultLocationRepository;

    private ApplicationProvider(Application application) {
        this.applicationContext = application.getApplicationContext();
    }

    public static void init(Application application) {
        if (INSTANCE == null) {
            if (application == null) {
                throw new NullPointerException("application instance is null!");
            } else {
                INSTANCE = new ApplicationProvider(application);
            }
        } else {
            throw new RuntimeException("application provider instantiated!");
        }
    }

    public static ApplicationProvider getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("application provider is not instantiated");
        } else {
            return INSTANCE;
        }
    }

    public LocationRepository getLocationRepositorySingleInstance(LocationDataSource locationDataSource) {
        if (defaultLocationRepository == null) {
            defaultLocationRepository = new DefaultLocationRepository(locationDataSource);
        }
        return defaultLocationRepository;
    }

    public LocationDataSourceImpl getLocationDataSource(FusedLocationProviderClient fusedLocationProviderClient, LocationRequest locationRequest) {
        return new LocationDataSourceImpl(fusedLocationProviderClient, locationRequest);
    }

    public FusedLocationProviderClient getFusedLocationProvider(Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    public LocationRequest getLocationRequest() {
        return new LocationRequest()
                .setInterval(LOCATION_REQUEST_INTERVAL_IN_MILLIS)
                .setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL_IN_MILLIS)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public Context getApplicationContext() {
        return applicationContext;
    }
}
