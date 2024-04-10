package com.safari.khourdineshan.ui.activity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.safari.khourdineshan.IServiceConnection;
import com.safari.khourdineshan.NavigatorService;
import com.safari.khourdineshan.R;
import com.safari.khourdineshan.core.mapper.LocationMapper;
import com.safari.khourdineshan.databinding.ActivityMainBinding;
import com.safari.khourdineshan.di.MainActivityProvider;
import com.safari.khourdineshan.utils.MapUtils;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2121;

    private ActivityMainBinding binding;
    private MainActivityViewModel mainActivityViewModel;
    private AlertDialog loadingDialog;
    private ArrayList<LatLng> decodedStepByStepRoute;
    private Polyline onMapPolyline;
    @Nullable
    private IServiceConnection serviceConnection;
    private Disposable locationObservationDisposable;
    private CompositeDisposable navigatorCompositeObservationDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityProvider.init();
        initMainActivityViewModel();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkLocationPermissionAndStartReceivingLocation();
        initClickListeners();
    }

    private void initClickListeners() {
        binding.map.setOnMapLongClickListener(latLng -> runOnUiThread(() -> mainActivityViewModel.onMapLongClicked(latLng)));
        binding.getRouteFab.setOnClickListener(v -> mainActivityViewModel.onGetRouteFabClicked());
        binding.currentLocationFab.setOnClickListener(v -> notifyViewModelOrPromptUserToEnableGPS());
        binding.startNavigationFab.setOnClickListener(v -> mainActivityViewModel.onStartNavigationButtonClicked());
        binding.endNavigationFab.setOnClickListener(v -> mainActivityViewModel.onEndNavigationButtonClicked());
    }

    private void notifyViewModelOrPromptUserToEnableGPS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mainActivityViewModel.isGpsProviderEnabled()) {
                mainActivityViewModel.onCurrentLocationFabClicked();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.enable_gps))
                        .setTitle(getString(R.string.precise_location_is_disabled));
                builder.setPositiveButton(getString(R.string.enable), (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .setNegativeButton(getString(R.string.cancel), (dialog, which) -> dialog.dismiss())
                        .show();
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void checkLocationPermissionAndStartReceivingLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mainActivityViewModel.startReceivingLocation();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mainActivityViewModel.startReceivingLocation();
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initMainActivityViewModel() {
        mainActivityViewModel = new ViewModelProvider(this, MainActivityProvider.getInstance().getMainActivityViewModelFactory()).get(MainActivityViewModel.class);
        locationObservationDisposable = mainActivityViewModel.getLocationObservable().subscribe(this::onNewLocationReceived, Throwable::printStackTrace);
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
                if (uiState instanceof MAP.SHOW_DROPPED_PIN.SHOW_DROPPED_PIN_AND_ROUTE_LOADING_DIALOG) {
                    showLoadingDialog();
                }
            } else if (uiState instanceof MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) {
                showRoutingState((MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN) uiState);
            }
        } else if (uiState instanceof NAVIGATION) {
            showNavigationState();
        }
    }

    private void showMapDroppedPinState(MAP.SHOW_DROPPED_PIN droppedPinState) {
        binding.map.setTilt(90,0);
        Marker droppedPinMarker = MainActivityProvider.getInstance().getDroppedPinMarker(this);
        binding.map.removeMarker(droppedPinMarker);
        droppedPinMarker.setLatLng(droppedPinState.getPinLatLng());
        binding.map.addMarker(droppedPinMarker);
        binding.getRouteFab.show();
        binding.startNavigationFab.hide();
        binding.currentLocationFab.show();
        binding.endNavigationFab.hide();
    }

    private void showNavigationState() {
        binding.getRouteFab.hide();
        binding.startNavigationFab.hide();
        binding.currentLocationFab.hide();
        binding.endNavigationFab.show();
        hideLoadingDialog();
        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder serviceIBinder) {
                NavigatorService.KhoordiNeshanServiceBinder binder = (NavigatorService.KhoordiNeshanServiceBinder) serviceIBinder;
                MainActivity.this.serviceConnection = binder.getServiceActions();

                if (navigatorCompositeObservationDisposable != null && !navigatorCompositeObservationDisposable.isDisposed()) {
                    navigatorCompositeObservationDisposable.dispose();
                }
                navigatorCompositeObservationDisposable = new CompositeDisposable();
                if (MainActivity.this.serviceConnection != null) {
                    binding.map.setTilt(65,0);
                    Disposable currentStepDisposable = MainActivity.this.serviceConnection.getNavigatorManager().currentStepObservable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(directionStep -> {
                                binding.navigatorCurrentStepTextView.setText(directionStep.getName());
                                Toast.makeText(MainActivity.this, directionStep.getName(), Toast.LENGTH_LONG).show();
                            }, Throwable::printStackTrace);

                    Disposable nextStepDisposable = MainActivity.this.serviceConnection.getNavigatorManager().nextStepObservable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(directionStep -> {
                                binding.navigatorNextStepTextView.setText(directionStep.getInstruction());
                                Toast.makeText(MainActivity.this, directionStep.getName(), Toast.LENGTH_LONG).show();
                            }, Throwable::printStackTrace);

                    Disposable snappedLocationDisposable = MainActivity.this.serviceConnection.getNavigatorManager().snappedLocationOnCurrentRoute()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(snappedLocation -> {
                                updateCurrentLocationMarkerLatLng(snappedLocation);
                                MapUtils.focusOnLocation(binding.map, snappedLocation);
                            }, Throwable::printStackTrace);

                    Disposable bearingDisposable = MainActivity.this.serviceConnection.getNavigatorManager().bearingBetweenLastAndCurrentSnappedLocationsObservable()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(bearing -> binding.map.setBearing(bearing.floatValue(), 1f), Throwable::printStackTrace);

                    navigatorCompositeObservationDisposable.addAll(currentStepDisposable, nextStepDisposable, snappedLocationDisposable);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                MainActivity.this.serviceConnection = null;
            }
        };

        bindService(new Intent(this, NavigatorService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void showRoutingState(MAP.SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN state) {
        binding.getRouteFab.hide();
        binding.startNavigationFab.show();
        binding.endNavigationFab.hide();
        binding.currentLocationFab.show();
        hideLoadingDialog();
        showRouteOnMap(state.getRoute());
        stopNavigation();
    }

    private void showMapUnfollowState() {
        binding.getRouteFab.hide();
        binding.startNavigationFab.hide();
        binding.endNavigationFab.hide();
        binding.currentLocationFab.show();
        hideRoute();
        hideLoadingDialog();
        stopNavigation();
    }

    private void showMapFollowState() {
        binding.map.setTilt(90,0);
        binding.getRouteFab.hide();
        binding.startNavigationFab.hide();
        binding.endNavigationFab.hide();
        binding.currentLocationFab.show();
        hideRoute();
        hideLoadingDialog();
        stopNavigation();
    }

    private void showLoadingDialog() {
        binding.getRouteFab.hide();
        binding.startNavigationFab.hide();
        binding.endNavigationFab.hide();
        binding.currentLocationFab.show();
        hideLoadingDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("getting route, please wait...")
                .setCancelable(false)
                .setNegativeButton("cancel", (dialog, which) -> {
                    mainActivityViewModel.cancelRoutingRequest();
                    dialog.dismiss();
                });
        loadingDialog = builder.create();
        loadingDialog.show();
        stopNavigation();
    }

    private void hideLoadingDialog() {
        if (loadingDialog != null) {
            loadingDialog.hide();
            loadingDialog = null;
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
        MapUtils.focusOnRoute(binding.map, PolylineEncoding.decode(route.getOverviewPolyline().getEncodedPolyline()));
    }

    private void stopNavigation() {
        if (serviceConnection != null) {
            serviceConnection.stop();
        }
    }

    private void hideRoute() {
        if (onMapPolyline != null) {
            binding.map.removePolyline(onMapPolyline);
            onMapPolyline = null;
        }
    }

    private void onNewLocationReceived(Location location) {
        if (mainActivityViewModel.getMapUIState().getValue() instanceof MAP) {
            updateCurrentLocationMarkerLatLng(location);
            if (mainActivityViewModel.getMapUIState().getValue() instanceof MAP.FOLLOW_USER_LOCATION) {
                MapUtils.focusOnLocation(binding.map, location);
            }
        }
    }

    private void updateCurrentLocationMarkerLatLng(Location location) {
        MainActivityProvider.getInstance().getCurrentLocationMarker(this).updateLatLng(LocationMapper.LocationToLatLng(location), binding.map);
    }

    @Override
    public void onBackPressed() {
        if (mainActivityViewModel.getMapUIState().getValue() instanceof MAP.FOLLOW_USER_LOCATION) {
            showCloseAppConfirmation();
        } else {
            mainActivityViewModel.onMainActivityBackPressed();
        }
    }

    private void showCloseAppConfirmation() {
        hideLoadingDialog();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.close_app))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    finish();
                    if (serviceConnection != null) {
                        serviceConnection.stop();
                    }
                })
                .setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.dismiss());
        loadingDialog = builder.create();
        loadingDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationObservationDisposable != null && !locationObservationDisposable.isDisposed()) {
            locationObservationDisposable.dispose();
        }
        MainActivityProvider.deinit();
    }
}