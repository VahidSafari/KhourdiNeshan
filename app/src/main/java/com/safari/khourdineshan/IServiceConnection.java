package com.safari.khourdineshan;

import com.safari.khourdineshan.data.navigator.NavigatorManager;

public interface IServiceConnection {
    void stop();
    NavigatorManager getNavigatorManager();
}
