package com.safari.khourdineshan;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.safari.khourdineshan.databinding.ActivityMainBinding;
import com.safari.khourdineshan.di.ApplicationProvider;
import com.safari.khourdineshan.utils.PermissionUtils;
import com.safari.khourdineshan.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2121;
    private ActivityMainBinding binding;
    KhoordiNeshanService khoordiNeshanService;
    private MainActivityViewModel mainActivityViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initMainActivityViewModel();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkLocationPermission();
        startService(new Intent(this, KhoordiNeshanService.class));
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
        Log.d("location", "" + location.getLatitude() + "-" + location.getLongitude());
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

}