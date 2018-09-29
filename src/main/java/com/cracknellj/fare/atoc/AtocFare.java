package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.FareDetail;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    public String getKey() {
        return getKey(fromNlc, toNlc, routeCode);
    }

    public static String getKey(String fromNlc, String toNlc, String routeCode) {
        return fromNlc + toNlc + routeCode;
    }
}
