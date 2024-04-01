package com.safari.khourdineshan;

import androidx.annotation.NonNull;

import com.safari.khourdineshan.data.navigator.repository.NavigatorManager;

public interface IServiceConnection {
    void stop();
    @NonNull
    NavigatorManager getNavigatorManager();
}
