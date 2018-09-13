package com.cracknellj.fare.objects;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

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

    public void add(Fare fare) {
        Preconditions.checkArgument(fare.fromId.equals(fromId));
        add(fare.toId, fare.fareDetail);
    }

    // TODO Does this even work. Check HAT -> BWK
    public void add(String toId, FareDetail fareDetailToAdd) {
        List<FareDetail> fareDetails = fares.computeIfAbsent(toId, x -> new ArrayList<>());
        for (int i = 0; i < fareDetails.size(); i++) {
            FareDetail fareDetailToReplace = fareDetails.get(i);
            if (fareDetailToAdd.equalsExceptForPrice(fareDetailToReplace)) {
                if (fareDetailToAdd.price < fareDetailToReplace.price) {
                    fareDetails.set(i, fareDetailToAdd);
                }
                return;
            }
        }
        fareDetails.add(fareDetailToAdd);
    }

    public List<Fare> toFareList() {
        List<Fare> faresList = new ArrayList<>();
        fares.forEach((toId, fareDetails) -> fareDetails.forEach(fd -> faresList.add(new Fare(fromId, toId, fd))));
        return faresList;
    }

    public void combineWith(FareSet fareSet2) {
        fareSet2.fares.forEach((toId, faresList) -> {
            if (fares.containsKey(toId)) {
                List<FareDetail> combinedFareDetails = new ArrayList<>(fares.size() + faresList.size());
                combinedFareDetails.addAll(fares.get(toId));
                combinedFareDetails.addAll(faresList);
                fares.put(toId, combinedFareDetails);
            } else {
                fares.put(toId, faresList);
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
