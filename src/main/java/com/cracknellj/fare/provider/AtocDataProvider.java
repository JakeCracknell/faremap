package com.cracknellj.fare.provider;

import com.cracknellj.fare.atoc.AtocDataReader;
import com.cracknellj.fare.objects.FareSet;

import java.util.HashMap;
import java.util.Map;

public class AtocDataProvider implements FareDataProvider {

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return new HashMap<>();
//        return new AtocDataReader().getFareSetsByStationId();
    }

}
