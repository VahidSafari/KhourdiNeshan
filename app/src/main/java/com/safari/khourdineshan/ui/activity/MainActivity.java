package com.safari.khourdineshan.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.carto.styles.AnimationStyleBuilder;
import com.carto.styles.AnimationType;
import com.carto.styles.MarkerStyle;
import com.carto.styles.MarkerStyleBuilder;
import com.carto.utils.BitmapUtils;
import com.safari.khourdineshan.KhoordiNeshanService;
import com.safari.khourdineshan.LocalBinder;
import com.safari.khourdineshan.R;
import com.safari.khourdineshan.databinding.ActivityMainBinding;
import com.safari.khourdineshan.di.ApplicationProvider;
import com.safari.khourdineshan.di.MainActivityProvider;
import com.safari.khourdineshan.utils.PermissionUtils;
import com.safari.khourdineshan.viewmodel.MainActivityViewModel;

import org.neshan.common.model.LatLng;
import org.neshan.mapsdk.model.Marker;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2121;

    private ActivityMainBinding binding;
    KhoordiNeshanService khoordiNeshanService;
    private MainActivityViewModel mainActivityViewModel;
    private Marker currentLocationMarker;
    private Marker droppedPinMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMainActivityViewModel();
        MainActivityProvider.init();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkLocationPermission();
        startService(new Intent(this, KhoordiNeshanService.class));
        initClickListeners();
    }

    private void initClickListeners() {
        binding.map.setOnMapLongClickListener(latLng -> {

        });
    }

    private void checkLocationPermission() {
        if (PermissionUtils.isFineLocationPermissionGranted(this)) {
            mainActivityViewModel.startReceivingLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mainActivityViewModel.startReceivingLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initMainActivityViewModel() {
        mainActivityViewModel = new ViewModelProvider(this, ApplicationProvider.getInstance().getMainActivityViewModelFactory()).get(MainActivityViewModel.class);
        mainActivityViewModel.getLiveLocation().observe(this, this::onNewLocationReceived);
    }

    private void onNewLocationReceived(Location location) {
        addUserMarker(new LatLng(location.getLatitude(), location.getLongitude()));
        focusOnUserLocation(location);
    }


    private Marker addMarker(LatLng loc) {
        // Creating animation for marker. We should use an object of type AnimationStyleBuilder, set
        // all animation features on it and then call buildStyle() method that returns an object of type
        // AnimationStyle
        AnimationStyleBuilder animStBl = new AnimationStyleBuilder();
        animStBl.setFadeAnimationType(AnimationType.ANIMATION_TYPE_SMOOTHSTEP);
        animStBl.setSizeAnimationType(AnimationType.ANIMATION_TYPE_SPRING);
        animStBl.setPhaseInDuration(0.5f);
        animStBl.setPhaseOutDuration(0.5f);
        animSt = animStBl.buildStyle();

        // Creating marker style. We should use an object of type MarkerStyleBuilder, set all features on it
        // and then call buildStyle method on it. This method returns an object of type MarkerStyle
        MarkerStyleBuilder markStCr = new MarkerStyleBuilder();
        markStCr.setSize(30f);
        markStCr.setBitmap(BitmapUtils.createBitmapFromAndroidBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker)));
        // AnimationStyle object - that was created before - is used here
        markStCr.setAnimationStyle(animSt);
        MarkerStyle markSt = markStCr.buildStyle();

        // Creating marker
        currentLocationMarker = new Marker(loc, markSt);

        // Adding marker to markerLayer, or showing marker on map!
        map.addMarker(marker);
        return marker;
    }

    public void focusOnUserLocation(Location location) {
            binding.map.moveCamera(
                    new LatLng(location.getLatitude(), location.getLongitude()), 0.25f);
            binding.map.setZoom(15, 0.25f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, KhoordiNeshanService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(connection);
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            khoordiNeshanService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivityProvider.deinit();
    }
}