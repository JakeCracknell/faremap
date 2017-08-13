package com.cracknellj.fare.atoc;

import com.cracknellj.fare.ws.objects.FareDetail;

public class AtocFare {
    public final String nlsFrom;
    public final String nlsTo;
    public final FareDetail fareDetail;

    public AtocFare(String nlsFrom, String nlsTo, FareDetail fareDetail) {
        this.nlsFrom = nlsFrom;
        this.nlsTo = nlsTo;
        this.fareDetail = fareDetail;
    }

    @Override
    public String toString() {
        return "AtocFare{" +
                "nlsFrom='" + nlsFrom + '\'' +
                ", nlsTo='" + nlsTo + '\'' +
                ", fareDetail=" + fareDetail +
                '}';
    }
}
