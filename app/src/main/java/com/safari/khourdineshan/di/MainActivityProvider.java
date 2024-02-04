package com.safari.khourdineshan.di;

import android.content.Context;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.safari.khourdineshan.R;
import com.safari.khourdineshan.core.BaseUrls;
import com.safari.khourdineshan.data.routing.datasource.RoutingService;

import org.neshan.mapsdk.model.Marker;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivityProvider {
    private static MainActivityProvider INSTANCE;
    private AnimationStyle markerAnimationStyle;
    private MarkerStyle currentLocationMarkerStyle;
    private MarkerStyle droppedPinMarkerStyle;
    private Marker currentLocationMarker;
    private Marker droppedPinMarker;
    private static final int REQUEST_TIME_OUT_IN_SECONDS = 10;

    private static RoutingService routingService;


    public static void init() {
        if (INSTANCE == null) {
            INSTANCE = new MainActivityProvider();
        } else {
            throw new RuntimeException("application provider instantiated!");
        }
    }

    public static MainActivityProvider getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("main activity provider is NOT instantiated");
        } else {
            return INSTANCE;
        }
    }

    public AnimationStyle getMarkerAnimationStyle() {
        if (markerAnimationStyle == null) {
            AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
            animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
            animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
            animStBl.setPhaseInDuration(0.5f);
            animStBl.setPhaseOutDuration(0.5f);
            markerAnimationStyle = animStBl.buildStyle();
        }
        return markerAnimationStyle;
    }

    public MarkerStyle getCurrentLocationMarkerStyle(Context context) {
        if (currentLocationMarkerStyle == null) {
            MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();
            markerStyleBuilder.setSize(25f);
            markerStyleBuilder.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.blue_circle)));
            markerStyleBuilder.setAnimationStyle(getMarkerAnimationStyle());
            currentLocationMarkerStyle = markerStyleBuilder.buildStyle();
        }
        return currentLocationMarkerStyle;
    }

    public MarkerStyle getDroppedPinMarkerStyle(Context context) {
        if (droppedPinMarkerStyle == null) {
            MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();
            markerStyleBuilder.setSize(30f);
            markerStyleBuilder.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_marker)));
            markerStyleBuilder.setAnimationStyle(getMarkerAnimationStyle());
            droppedPinMarkerStyle = markerStyleBuilder.buildStyle();
        }
        return droppedPinMarkerStyle;
    }

    public Marker getCurrentLocationMarker(Context context) {
        if (currentLocationMarker == null) {
            currentLocationMarker = new Marker(null, getCurrentLocationMarkerStyle(context));
        }
        return currentLocationMarker;
    }

    public Marker getDroppedPinMarker(Context context) {
        if (droppedPinMarker == null) {
            droppedPinMarker = new Marker(null, getCurrentLocationMarkerStyle(context));
        }
        return droppedPinMarker;
    }

    @NonNull
    private static OkHttpClient getOkhttpClient() {
        return new OkHttpClient.Builder()
                .readTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .connectTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .callTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(REQUEST_TIME_OUT_IN_SECONDS, TimeUnit.SECONDS)
                .build();
    }

    @NonNull
    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .client(getOkhttpClient())
                .baseUrl(BaseUrls.ROUTING_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @NonNull
    public static RoutingService getRoutingService() {
        if (routingService == null) {
            routingService = getRetrofit().create(RoutingService.class);
        }
        return routingService;
    }

    public static void deinit() {
        INSTANCE = null;
    }

}
