package com.safari.khourdineshan.di;

import com.safari.khourdineshan.data.navigator.DefaultNavigatorManager;
import com.safari.khourdineshan.data.navigator.NavigatorManager;

public class NavigatorProvider {

    private static NavigatorProvider INSTANCE;

    private final ApplicationProvider applicationProvider;

    private NavigatorManager navigatorManagerSingleton;

    private NavigatorProvider(ApplicationProvider applicationProvider) {
        this.applicationProvider = applicationProvider;
    }

    public static void init(ApplicationProvider applicationProvider) {
        if (INSTANCE == null) {
            if (applicationProvider == null) {
                throw new NullPointerException("application provider instance is null!");
            } else {
                INSTANCE = new NavigatorProvider(applicationProvider);
            }
        } else {
            throw new RuntimeException("navigator provider instantiated already!");
        }
    }

    public static NavigatorProvider getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("NavigatorProvider is not instantiated");
        } else {
            return INSTANCE;
        }
    }

    public NavigatorManager getNavigatorManagerSingleton() {
        if (navigatorManagerSingleton == null) {
            navigatorManagerSingleton = new DefaultNavigatorManager(applicationProvider.getRoutingRepositorySingleton(), applicationProvider.getLocationRepositorySingleton());
        }
        return navigatorManagerSingleton;
    }

    public void deInit() {
        INSTANCE.getNavigatorManagerSingleton().stop();
        INSTANCE = null;
    }





}
