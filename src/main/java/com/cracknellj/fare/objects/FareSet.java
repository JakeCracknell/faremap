package com.cracknellj.fare.objects;

import jersey.repackaged.com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FareSet {
    public final String fromId;
    public final Map<String, List<FareDetail>> fares;

    public FareSet(String fromId, Map<String, List<FareDetail>> fares) {
        this.fromId = fromId;
        this.fares = fares;
    }

    public FareSet(String fromId) {
        this(fromId, new HashMap<>());
    }

    public void add(String toId, FareDetail fareDetail) {
        fares.computeIfAbsent(toId, x -> new ArrayList<>()).add(fareDetail);
    }

    public static FareSet combine(FareSet fareSet1, FareSet fareSet2) {
        Map<String, List<FareDetail>> faresMap = new HashMap<>(fareSet1.fares);
        fareSet2.fares.forEach((toId, fares) -> {
            if (faresMap.containsKey(toId)) {
                faresMap.get(toId).addAll(fares);
            } else {
                faresMap.put(toId, fares);
            }
        });
        return new FareSet(fareSet1.fromId, faresMap);
    }

    public static Map<String, FareSet> combine(Map<String, FareSet> fareSetMap1, Map<String, FareSet> fareSetMap2) {
        Map<String, FareSet> fareSetMap = new HashMap<>();
        for (String fromId : Sets.union(fareSetMap1.keySet(), fareSetMap2.keySet())) {
            FareSet fareSet1 = fareSetMap1.getOrDefault(fromId, new FareSet(fromId));
            FareSet fareSet2 = fareSetMap2.getOrDefault(fromId, new FareSet(fromId));
            fareSetMap.put(fromId, combine(fareSet1, fareSet2));
        }
        return fareSetMap;
    }
}
