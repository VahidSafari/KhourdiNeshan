package com.safari.khourdineshan.di;

import android.app.Application;
import android.content.Context;
import android.location.LocationManager;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.safari.khourdineshan.BuildConfig;
import com.safari.khourdineshan.core.AuthorizationInterceptor;
import com.safari.khourdineshan.core.BaseUrls;
import com.safari.khourdineshan.data.location.datasource.LocationDataSourceImpl;
import com.safari.khourdineshan.data.location.repository.DefaultLocationRepository;
import com.safari.khourdineshan.data.location.repository.LocationRepository;
import com.safari.khourdineshan.data.routing.datasource.RoutingService;
import com.safari.khourdineshan.data.routing.repository.DefaultRoutingRepository;
import com.safari.khourdineshan.data.routing.repository.RoutingRepository;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApplicationProvider {

    private static final int LOCATION_REQUEST_INTERVAL_IN_MILLIS = 1000;
    private static final int LOCATION_REQUEST_FASTEST_INTERVAL_IN_MILLIS = 500;
    private static final int REQUEST_TIME_OUT_IN_SECONDS = 10;

    private static ApplicationProvider INSTANCE;
    private final Context applicationContext;
    private LocationRepository defaultLocationRepository;
    private RoutingRepository routingRepository;
    private Retrofit retrofit;

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

    public LocationRepository getLocationRepositorySingleton() {
        if (defaultLocationRepository == null) {
            defaultLocationRepository =
                    new DefaultLocationRepository(
                            getLocationDataSource(getFusedLocationProvider(getApplicationContext()),
                                    getLocationManager(getApplicationContext()),
                                    getLocationRequest())
                    );
        }
        return defaultLocationRepository;
    }

    public LocationManager getLocationManager(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public LocationDataSourceImpl getLocationDataSource(FusedLocationProviderClient fusedLocationProviderClient, LocationManager locationManager, LocationRequest locationRequest) {
        return new LocationDataSourceImpl(fusedLocationProviderClient, locationManager, locationRequest);
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

    @NonNull
    public RoutingRepository getRoutingRepositorySingleton() {
        if (routingRepository == null) {
            routingRepository = new DefaultRoutingRepository(getRoutingService());
        }
        return routingRepository;
    }

    @NonNull
    public RoutingService getRoutingService() {
        return getSingletonRetrofit().create(RoutingService.class);
    }

    @NonNull
    private Retrofit getSingletonRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .client(getOkhttpClient())
                    .baseUrl(BaseUrls.ROUTING_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    @NonNull
    private OkHttpClient getOkhttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .callTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(getAuthorizationInterceptor()).build();
    }

    private AuthorizationInterceptor getAuthorizationInterceptor() {
        return new AuthorizationInterceptor(BuildConfig.API_KEY);
    }

    public Context getApplicationContext() {
        return applicationContext;
    }
}
