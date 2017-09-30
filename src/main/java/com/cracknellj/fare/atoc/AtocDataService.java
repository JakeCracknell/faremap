package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtocDataService {
    private final AtocDataReader atocDataReader;

    private static AtocDataService ourInstance = new AtocDataService();

    public static AtocDataService getInstance() {
        return ourInstance;
    }

    private AtocDataService() {
        atocDataReader = new AtocDataReader();
    }

    public FareSet getFaresFrom(String fromId) {
        Map<String, List<FareDetail>> fares = atocDataReader.getFaresByStationId().getOrDefault(fromId, new HashMap<>());
        return new FareSet(fromId, fares);
    }

}
