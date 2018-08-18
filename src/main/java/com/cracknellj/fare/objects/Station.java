package com.cracknellj.fare.objects;

import java.util.EnumSet;

public class Station {
    public final String stationId;
    public final String stationName;
    public final EnumSet<StationTag> tags;
    public final float latitude;
    public final float longitude;
    public final String crs;

    public Station(String stationId, String stationName, EnumSet<StationTag> tags, float latitude, float longitude, String crs) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.crs = crs;
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationId='" + stationId + '\'' +
                ", stationName='" + stationName + '\'' +
                '}';
    }
}
