package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public interface FareDataProvider {
    Map<String, FareSet> getAllFareSets();

    default FareSet getFaresFrom(String fromId) {
        return getAllFareSets().getOrDefault(fromId, new FareSet(fromId));
    }

    default List<FareDetail> getFares(String fromId, String toId) {
        FareSet fareSet = getAllFareSets().get(fromId);
        if (fareSet != null) {
            List<FareDetail> fareDetails = fareSet.fares.get(toId);
            if (fareDetails != null) {
                return fareDetails;
            }
        }
        return Collections.emptyList();
    }
}
