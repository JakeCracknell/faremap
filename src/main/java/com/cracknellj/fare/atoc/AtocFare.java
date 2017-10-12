package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.FareDetail;

public class AtocFare {
    public final String fromNlc;
    public final String toNlc;
    public final boolean reversible;
    public final String routeCode;
    public final FareDetail fareDetail;

    public AtocFare(String fromNlc, String toNlc, boolean reversible, String routeCode, FareDetail fareDetail) {
        this.fromNlc = fromNlc;
        this.toNlc = toNlc;
        this.reversible = reversible;
        this.routeCode = routeCode;
        this.fareDetail = fareDetail;
    }
}
