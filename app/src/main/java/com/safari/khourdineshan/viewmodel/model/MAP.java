package com.safari.khourdineshan.viewmodel.model;

import org.neshan.common.model.LatLng;
import org.neshan.servicessdk.direction.model.Route;

public abstract class MAP extends UIState {

    public static final class FOLLOW_USER_LOCATION extends MAP {
    }

    public static final class DO_NOT_FOLLOW_USER_LOCATION extends MAP {
    }

    public static final class SHOW_DROPPED_PIN extends MAP {
        private final LatLng pinLatLng;

        public SHOW_DROPPED_PIN(LatLng pinLatLng) {
            this.pinLatLng = pinLatLng;
        }

        public LatLng getPinLatLng() {
            return pinLatLng;
        }
    }

    public static final class WAITING_FOR_ROUTE_RESPONSE extends MAP {
    }

    public static final class SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN extends MAP {
        private final Route route;

        public SHOW_ROUTE_BETWEEN_USER_LOCATION_AND_DROPPED_PIN(Route route) {
            this.route = route;
        }

        public Route getRoute() {
            return route;
        }
    }
}
