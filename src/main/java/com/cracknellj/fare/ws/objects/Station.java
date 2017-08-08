package com.cracknellj.fare.ws.objects;

import java.util.List;

public class Station {
    private final String stationId;
    private final String stationName;
    private final List<String> modes;
    private final String type;
    private final boolean oysterAccepted;
    private final float latitude;
    private final float longitude;

    public Station(String stationId, String stationName, List<String> modes, boolean oysterAccepted, float latitude, float longitude) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.modes = modes;
        this.oysterAccepted = oysterAccepted;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = modes.get(0);
    }
}
