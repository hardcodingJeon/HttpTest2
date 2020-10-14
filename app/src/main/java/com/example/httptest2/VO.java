package com.example.httptest2;

import java.util.ArrayList;

public class VO {

    ArrayList<member> device;

    public VO() {
    }

    public VO(ArrayList<member> device) {
        this.device = device;
    }

    public class member {
        String device_id;
        String last_latitude;
        String last_longitude;
        String last_device_battery;
        String last_bike_battery;
        String bike_error_code;

        public member() {
        }

        public member(String device_id, String last_latitude, String last_longitude, String last_device_battery, String last_bike_battery, String bike_error_code) {
            this.device_id = device_id;
            this.last_latitude = last_latitude;
            this.last_longitude = last_longitude;
            this.last_device_battery = last_device_battery;
            this.last_bike_battery = last_bike_battery;
            this.bike_error_code = bike_error_code;
        }
    }
}
