package com.safari.khourdineshan.data.navigator;

import android.location.Location;

import androidx.lifecycle.LiveData;

import org.neshan.servicessdk.direction.model.DirectionStep;

public interface NavigatorManager {
    void start();
    LiveData<Location> snappedLocationOnCurrentRoute();
    LiveData<DirectionStep> currentStep();
    LiveData<DirectionStep> NextStep();
    void stop();
}
