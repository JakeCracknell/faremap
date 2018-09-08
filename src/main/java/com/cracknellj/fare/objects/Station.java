package com.cracknellj.fare.objects;

import java.util.EnumSet;

public class Station {
    public String stationId;
    public String stationName;
    public EnumSet<StationTag> tags;
    public float latitude;
    public float longitude;
    public String crs;
    public int yearlyUsage;

    public Station(String stationId, String stationName, EnumSet<StationTag> tags, float latitude, float longitude, String crs, int yearlyUsage) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.tags = tags;
        this.latitude = latitude;
        this.longitude = longitude;
        this.crs = crs;
        this.yearlyUsage = yearlyUsage;
    }

    @Override
    public String toString() {
        return "Station{" +
                "stationId='" + stationId + '\'' +
                ", stationName='" + stationName + '\'' +
                '}';
    }
}
