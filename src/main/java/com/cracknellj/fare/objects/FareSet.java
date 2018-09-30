package com.cracknellj.fare.objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FareSet {
    public final String fromId;
    public final Map<String, FareDetailCollection> fares;

    public FareSet(String fromId, Map<String, FareDetailCollection> fares) {
        this.fromId = fromId;
        this.fares = fares;
    }

    public FareSet(String fromId) {
        this(fromId, new HashMap<>());
    }

    public void add(Fare fare) {
        Preconditions.checkArgument(fare.fromId.equals(fromId));
        add(fare.toId, fare.fareDetail);
    }

    public void add(String toId, FareDetail fareDetailToAdd) {
        List<FareDetail> fareDetails = fares.computeIfAbsent(toId, x -> new FareDetailCollection());
        fareDetails.add(fareDetailToAdd);
    }

    public void combineWith(FareSet fareSet2) {
        fareSet2.fares.forEach((toId, extraFaresList) -> {
            if (fares.containsKey(toId)) {
                FareDetailCollection thisFaresList = fares.get(toId);
                FareDetailCollection combinedFareDetails = new FareDetailCollection(thisFaresList.size() + extraFaresList.size());
                combinedFareDetails.addAll(thisFaresList);
                combinedFareDetails.addAll(extraFaresList);
                fares.put(toId, combinedFareDetails);
            } else {
                fares.put(toId, extraFaresList);
            }
        });
    }

    public static Map<String, FareSet> combine(Map<String, FareSet> fareSetMap1, Map<String, FareSet> fareSetMap2) {
        Map<String, FareSet> fareSetMap = new HashMap<>();
        for (String fromId : Sets.union(fareSetMap1.keySet(), fareSetMap2.keySet())) {
            FareSet combinedFareSet = fareSetMap1.getOrDefault(fromId, new FareSet(fromId));
            combinedFareSet.combineWith(fareSetMap2.getOrDefault(fromId, new FareSet(fromId)));
            fareSetMap.put(fromId, combinedFareSet);
        }
        return fareSetMap;
    }

    public static FareSet combine(FareSet fareSet1, FareSet fareSet2) {
        fareSet1.combineWith(fareSet2);
        return fareSet1;
    }

}
