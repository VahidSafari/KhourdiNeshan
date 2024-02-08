package com.safari.khourdineshan.viewmodel.model;

import org.neshan.servicessdk.direction.model.Route;

public final class SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN extends MapUIState {
    private Route route;

    public SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN(Route route) {
        this.route = route;
    }

    public Route getRoute() {
        return route;
    }
}
