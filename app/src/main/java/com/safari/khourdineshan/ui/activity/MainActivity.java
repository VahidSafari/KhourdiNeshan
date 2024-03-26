package com.safari.khourdineshan.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.safari.khourdineshan.KhoordiNeshanService;
import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.databinding.ActivityMainBinding;
import com.safari.khourdineshan.di.MainActivityProvider;
import com.safari.khourdineshan.utils.MapUtils;
import com.safari.khourdineshan.utils.PermissionUtils;
import com.safari.khourdineshan.viewmodel.MainActivityViewModel;
import com.safari.khourdineshan.viewmodel.model.MAP;
import com.safari.khourdineshan.viewmodel.model.NAVIGATION;
import com.safari.khourdineshan.viewmodel.model.UIState;

import org.neshan.common.model.LatLng;
import org.neshan.common.utils.PolylineEncoding;
import org.neshan.mapsdk.model.Marker;
import org.neshan.mapsdk.model.Polyline;
import org.neshan.servicessdk.direction.model.DirectionStep;
import org.neshan.servicessdk.direction.model.Route;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2121;

    private ActivityMainBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private AlertDialog loadingDialog;
    private ArrayList<LatLng> decodedStepByStepRoute;
    private Polyline onMapPolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityProvider.init();
        initMainActivityViewModel();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkLocationPermission();
        startService(new Intent(this, KhoordiNeshanService.class));
        initClickListeners();
    }

    private void initClickListeners() {
        binding.map.setOnMapLongClickListener(latLng -> runOnUiThread(() -> mainActivityViewModel.onMapLongClicked(latLng)));
        binding.map.setOnMapClickListener(latLng -> runOnUiThread(() -> mainActivityViewModel.onMapClicked(latLng)));
        binding.getRouteFab.setOnClickListener(v -> mainActivityViewModel.requestForRoute());
        binding.currentLocationFab.setOnClickListener(v -> mainActivityViewModel.onCurrentLocationFabClicked());
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
        mainActivityViewModel = new ViewModelProvider(this, MainActivityProvider.getInstance().getMainActivityViewModelFactory()).get(MainActivityViewModel.class);
        mainActivityViewModel.getLiveLocation().observe(this, this::onNewLocationReceived);
        mainActivityViewModel.getMapUIState().observe(this, this::onMapUiStateChanged);
    }

    private void onMapUiStateChanged(UIState uiState) {
        if (uiState instanceof MAP) {
            if (uiState instanceof MAP.FOLLOW_USER_LOCATION) {
                showMapFollowState();
            } else if (uiState instanceof MAP.DO_NOT_FOLLOW_USER_LOCATION) {
                showMapUnfollowState();
            } else if (uiState instanceof MAP.SHOW_DROPPED_PIN) {
                showMapDroppedPinState((MAP.SHOW_DROPPED_PIN) uiState);
            } else if (uiState instanceof MAP.WAITING_FOR_ROUTE_RESPONSE) {
                showLoadingState();
            } else if (uiState instanceof MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) {
                showRoutingState((MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) uiState);
            }
        } else if (uiState instanceof NAVIGATION) {
            showNavigationState();
        }
    }

    private void showMapDroppedPinState(MAP.SHOW_DROPPED_PIN droppedPinState) {
        Marker droppedPinMarker = MainActivityProvider.getInstance().getDroppedPinMarker(this);
        binding.map.removeMarker(droppedPinMarker);
        droppedPinMarker.setLatLng(droppedPinState.getPinLatLng());
        binding.map.addMarker(droppedPinMarker);
    }

    private void showNavigationState() {
        binding.getRouteFab.hide();
        hideLoadingState();
    }

    private void showRoutingState(MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN state) {
        binding.getRouteFab.hide();
        hideLoadingState();
        showRouteOnMap(state.getRoute());
    }

    private void showMapUnfollowState() {
        hideRoute();
        hideLoadingState();
    }

    private void showMapFollowState() {
        binding.getRouteFab.hide();
        hideRoute();
        hideLoadingState();
    }

    private void showLoadingState() {
        hideLoadingState();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("getting route, please wait...")
                .setCancelable(false)
                .setNegativeButton("cancel", (dialog, which) -> {
                    mainActivityViewModel.cancelRoutingRequest();
                    dialog.dismiss();
                });
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingState() {
        if (loadingDialog != null) {
            loadingDialog.hide();
        }
    }

    private void showRouteOnMap(Route route) {
        // DECODE
        decodedStepByStepRoute = new ArrayList<>();
        for (DirectionStep step : route.getLegs().get(0).getDirectionSteps()) {
            decodedStepByStepRoute.addAll(PolylineEncoding.decode(step.getEncodedPolyline()));
        }

        // HIDE PREVIOUS ROUTE
        hideRoute();

        // SHOW NEW ROUTE
        onMapPolyline = MainActivityProvider.getInstance().getPolyline(decodedStepByStepRoute);
        binding.map.addPolyline(onMapPolyline);

        // FOCUS ON ROUTE
        MapUtils.focusOnRectangleOfTwoPoints(binding.map, MainActivityProvider.getInstance().getCurrentLocationMarker(this).getLatLng(), MainActivityProvider.getInstance().getDroppedPinMarker(this).getLatLng());
    }


    private void hideRoute() {
        if (onMapPolyline != null) {
            binding.map.removePolyline(onMapPolyline);
            onMapPolyline = null;
        }
    }

    private void onNewLocationReceived(Location location) {
        MainActivityProvider.getInstance().getCurrentLocationMarker(this).updateLatLng(LocationMapper.LocationToLatLng(location), binding.map);
        if (mainActivityViewModel.getMapUIState().getValue() instanceof MAP.FOLLOW_USER_LOCATION) {
            MapUtils.focusOnLocation(binding.map, location);
        }
    }

    @Override
    public void onBackPressed() {
        if (mainActivityViewModel.getMapUIState().getValue() instanceof MAP.FOLLOW_USER_LOCATION) {
            super.onBackPressed();
        } else {
            mainActivityViewModel.onMainActivityBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivityProvider.deinit();
    }
}