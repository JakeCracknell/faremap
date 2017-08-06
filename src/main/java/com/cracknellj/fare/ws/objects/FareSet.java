package com.cracknellj.fare.ws.objects;

import java.util.List;
import java.util.Map;

public class FareSet {
    private final String fromId;
    private final Map<String, List<FareDetail>> fares;

    public FareSet(String fromId, Map<String, List<FareDetail>> fares) {
        this.fromId = fromId;
        this.fares = fares;
    }
}
