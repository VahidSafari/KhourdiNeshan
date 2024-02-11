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
import com.safari.khourdineshan.databinding.ActivityMainBinding;
import com.safari.khourdineshan.di.MainActivityProvider;
import com.safari.khourdineshan.utils.MapUtils;
import com.safari.khourdineshan.utils.PermissionUtils;
import com.safari.khourdineshan.viewmodel.MainActivityViewModel;
import com.safari.khourdineshan.viewmodel.model.DO_NOT_FOLLOW_USER_LOCATION;
import com.safari.khourdineshan.viewmodel.model.FOLLOW_USER_LOCATION;
import com.safari.khourdineshan.viewmodel.model.MapUIState;
import com.safari.khourdineshan.viewmodel.model.NAVIGATION;
import com.safari.khourdineshan.viewmodel.model.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN;
import com.safari.khourdineshan.viewmodel.model.WAITING_FOR_ROUTE_RESPONSE;

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
    private ArrayList<LatLng> routeOverviewPolylinePoints;
    private ArrayList<LatLng> decodedStepByStepPath;
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
        binding.map.setOnMapLongClickListener(latLng -> mainActivityViewModel.onMapLongClicked(latLng));
        binding.map.setOnMapClickListener(latLng -> mainActivityViewModel.onMapClicked(latLng));
        binding.getRouteFab.setOnClickListener(v -> mainActivityViewModel.requestForRoute());
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
        mainActivityViewModel.getDroppedPinLatLng().observe(this, this::onDroppedPinLatLngChanged);
    }

    private void onDroppedPinLatLngChanged(LatLng latLng) {
        Marker droppedPinMarker = MainActivityProvider.getInstance().getDroppedPinMarker(this);
        binding.map.removeMarker(droppedPinMarker);
        droppedPinMarker.setLatLng(latLng);
        binding.map.addMarker(droppedPinMarker);
    }

    private void onMapUiStateChanged(MapUIState mapUIState) {
        if (mapUIState instanceof FOLLOW_USER_LOCATION) {
            showMapFollowState();
        } else if (mapUIState instanceof DO_NOT_FOLLOW_USER_LOCATION) {
            showMapUnfollowState();
        } else if (mapUIState instanceof SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) {
            showRoutingState((SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) mapUIState);
        } else if (mapUIState instanceof NAVIGATION) {
            showNavigationState();
        } else if (mapUIState instanceof WAITING_FOR_ROUTE_RESPONSE) {
            showLoadingState();
        }
    }

    private void showNavigationState() {
        hideLoadingState();
    }

    private void showRoutingState(SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN state) {
        hideLoadingState();
        showRouteOnMap(state.getRoute());
    }

    private void showMapUnfollowState() {
        hideLoadingState();
    }

    private void showMapFollowState() {
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
        routeOverviewPolylinePoints = new ArrayList<>(PolylineEncoding.decode(route.getOverviewPolyline().getEncodedPolyline()));
        decodedStepByStepPath = new ArrayList<>();

        // decoding each segment of steps and putting to an array
        for (DirectionStep step : route.getLegs().get(0).getDirectionSteps()) {
            decodedStepByStepPath.addAll(PolylineEncoding.decode(step.getEncodedPolyline()));
        }

        if (onMapPolyline != null) {
            binding.map.removePolyline(onMapPolyline);
        }
        onMapPolyline = new Polyline(routeOverviewPolylinePoints, MainActivityProvider.getInstance().getLineStyle());
        //draw polyline between route points
        binding.map.addPolyline(onMapPolyline);
        // focusing camera on first point of drawn line
        MapUtils.focusOnRectangleOfTwoPoints(binding.map, MainActivityProvider.getInstance().getCurrentLocationMarker(this).getLatLng(), MainActivityProvider.getInstance().getDroppedPinMarker(this).getLatLng());
    }

    private void onNewLocationReceived(Location location) {
        MapUtils.updateMarkerLocation(binding.map, MainActivityProvider.getInstance().getCurrentLocationMarker(this), location);
        if (mainActivityViewModel.getMapUIState().getValue() instanceof FOLLOW_USER_LOCATION) {
            MapUtils.focusOnLocation(binding.map, location);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivityProvider.deinit();
    }
}