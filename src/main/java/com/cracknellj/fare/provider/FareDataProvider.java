package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareSet;

import java.util.Map;

public interface FareDataProvider {
    Map<String, FareSet> getAllFareSets();

    default FareSet getFaresFrom(String fromId) {
        return getAllFareSets().getOrDefault(fromId, new FareSet(fromId));
    }
}
