package com.cracknellj.fare.objects;

public class Fare {
    public final String fromId;
    public final String toId;
    public final FareDetail fareDetail;

    public Fare(String fromId, String toId, FareDetail fareDetail) {
        this.fromId = fromId;
        this.toId = toId;
        this.fareDetail = fareDetail;
    }

    @Override
    public String toString() {
        return "Fare{" +
                "fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", fareDetail=" + fareDetail +
                '}';
    }
}
