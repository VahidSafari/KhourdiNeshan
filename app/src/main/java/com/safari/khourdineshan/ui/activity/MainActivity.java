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
import com.safari.khourdineshan.di.ApplicationProvider;
import com.safari.khourdineshan.di.MainActivityProvider;
import com.safari.khourdineshan.utils.MapUtils;
import com.safari.khourdineshan.utils.PermissionUtils;
import com.safari.khourdineshan.viewmodel.MainActivityViewModel;
import com.safari.khourdineshan.viewmodel.MapUIState;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2121;

    private ActivityMainBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private AlertDialog loadingDialog;

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
        binding.map.setOnMapLongClickListener(latLng -> mainActivityViewModel.onMapLongClicked(latLng));
        binding.map.setOnMapClickListener(latLng -> mainActivityViewModel.onMapClicked(latLng));
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
        mainActivityViewModel.getMapUIState().observe(this, this::onMapUiStateChanged);
    }

    private void onMapUiStateChanged(MapUIState mapUIState) {
        switch (mapUIState) {
            case FOLLOW_USER_LOCATION:
                showMapFollowState();
                break;
            case DO_NOT_FOLLOW_USER_LOCATION:
                showMapUnfollowState();
                break;
            case SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN:
                showRoutingState();
                break;
            case NAVIGATION:
                showNavigationState();
                break;
            case WAITING_FOR_ROUTE_RESPONSE:
                showLoadingState();
                break;
            default:
                break;
        }
    }

    private void showNavigationState() {
        hideLoadingState();
    }

    private void showRoutingState() {
        hideLoadingState();
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
                .setCancelable(false);
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    private void hideLoadingState() {
        if (loadingDialog != null) {
            loadingDialog.hide();
        }
    }

    private void onNewLocationReceived(Location location) {
        MapUtils.updateMarkerLocation(binding.map, MainActivityProvider.getInstance().getCurrentLocationMarker(this), location);
        if (mainActivityViewModel.getMapUIState().getValue() == MapUIState.FOLLOW_USER_LOCATION) {
            MapUtils.focusOnLocation(binding.map, location);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainActivityProvider.deinit();
    }
}