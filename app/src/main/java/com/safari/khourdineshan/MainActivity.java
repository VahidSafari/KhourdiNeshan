package com.safari.khourdineshan;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.safari.khourdineshan.databinding.ActivityMainBinding;
import com.safari.khourdineshan.utils.PermissionUtils;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2121;
    private ActivityMainBinding binding;
    KhoordiNeshanService khoordiNeshanService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        checkLocationPermission();
        initUiComponentListeners();
        startService(new Intent(this, KhoordiNeshanService.class));
    }

    private void initUiComponentListeners() {

    }

    private void checkLocationPermission() {
        if (PermissionUtils.isFineLocationPermissionGranted(this)) {
            // Permission is already granted
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        }

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
        public void onServiceDisconnected(ComponentName arg0) {}
    };

}