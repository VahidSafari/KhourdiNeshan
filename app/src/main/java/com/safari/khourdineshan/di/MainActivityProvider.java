package com.safari.khourdineshan.di;

import android.content.Context;


import com.carto.styles.AnimationStyle;
import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;

import org.neshan.mapsdk.model.Marker;

public class MainActivityProvider {
    private static MainActivityProvider INSTANCE;
    private Context mainActivityContext;
    private AnimationStyle markerAnimationStyle;
    private MarkerStyle currentLocationMarkerStyle;
    private MarkerStyle droppedPinMarkerStyle;
    private Marker currentLocationMarker;

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

    public MarkerStyle getCurrentLocationMarkerStyle() {
        if (currentLocationMarkerStyle == null) {
            MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();
        }
        return currentLocationMarkerStyle;
    }

    public MarkerStyle getDroppedPinMarkerStyle() {
        if (droppedPinMarkerStyle == null) {
            MarkerStyleBuilder markerStyleBuilder = new MarkerStyleBuilder();

        }
        return droppedPinMarkerStyle;
    }



    public static void deinit() {
        INSTANCE = null;
    }

}
