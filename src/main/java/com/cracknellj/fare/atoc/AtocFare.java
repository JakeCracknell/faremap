package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.FareDetail;

public class AtocFare {
    public final String fromNlc;
    public final String toNlc;
    public final String routeCode;
    public final FareDetail fareDetail;

    public AtocFare(String fromNlc, String toNlc, String routeCode, FareDetail fareDetail) {
        this.fromNlc = fromNlc;
        this.toNlc = toNlc;
        this.routeCode = routeCode;
        this.fareDetail = fareDetail;
    }

    public static String getKey(String fromNlc, String toNlc, String routeCode) {
        return fromNlc + toNlc + routeCode;
    }

    public String getKey() {
        return getKey(fromNlc, toNlc, routeCode);
    }
}
