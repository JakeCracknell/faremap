package com.cracknellj.fare.provider;

import com.cracknellj.fare.atoc.AtocDataReader;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtocDataProvider implements FareDataProvider {
    private final AtocDataReader atocDataReader;

    public AtocDataProvider() {
        atocDataReader = new AtocDataReader();
    }

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return atocDataReader.getFareSetsByStationId();
    }

}
