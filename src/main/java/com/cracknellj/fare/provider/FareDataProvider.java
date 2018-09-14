package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareDetailCollection;
import com.cracknellj.fare.objects.FareSet;

import java.util.Map;

public interface FareDataProvider {
    Map<String, FareSet> getAllFareSets();

    default FareSet getFaresFrom(String fromId) {
        return getAllFareSets().getOrDefault(fromId, new FareSet(fromId));
    }

    default FareDetailCollection getFares(String fromId, String toId) {
        FareSet fareSet = getAllFareSets().get(fromId);
        if (fareSet != null) {
            FareDetailCollection fareDetails = fareSet.fares.get(toId);
            if (fareDetails != null) {
                return fareDetails;
            }
        }
        return FareDetailCollection.empty();
    }
}
