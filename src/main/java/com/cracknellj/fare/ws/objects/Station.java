package com.cracknellj.fare.ws.objects;

import java.util.List;

public class Station {
    public final String stationId;
    public final String stationName;
    public final List<String> modes;
    public final boolean oysterAccepted;
    public final float latitude;
    public final float longitude;
    public final String crs;

    public Station(String stationId, String stationName, List<String> modes, boolean oysterAccepted, float latitude, float longitude, String crs) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.modes = modes;
        this.oysterAccepted = oysterAccepted;
        this.latitude = latitude;
        this.longitude = longitude;
        this.crs = crs;
    }
}
