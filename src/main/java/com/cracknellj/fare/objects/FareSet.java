package com.cracknellj.fare.objects;

import java.util.List;
import java.util.Map;

public class FareSet {
    public final String fromId;
    public final Map<String, List<FareDetail>> fares;

    public FareSet(String fromId, Map<String, List<FareDetail>> fares) {
        this.fromId = fromId;
        this.fares = fares;
    }
}
