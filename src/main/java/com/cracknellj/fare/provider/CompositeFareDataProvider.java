package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareDetailCollection;
import com.cracknellj.fare.objects.FareSet;

import java.util.Map;
import java.util.stream.Stream;

public class CompositeFareDataProvider implements FareDataProvider {
    private final Map<String, FareSet> fareSets;

    private CompositeFareDataProvider(Map<String, FareSet> fareSets) {
        this.fareSets = fareSets;
        fareSets.values().parallelStream().forEach(fareSet -> fareSet.fares.values()
                .forEach(FareDetailCollection::calculateAndCacheCheapestFares));
    }

    public static CompositeFareDataProvider load() {
        return new CompositeFareDataProvider(
                Stream.of(new AtocDataProvider(), new TFLDataProvider(), new WalkingFareDataProvider()).parallel()
                        .map(FareDataProvider::getAllFareSets).reduce(FareSet::combine)
                        .orElseThrow(() -> new RuntimeException("Failed to load any fare datas"))
        );
    }

    public void add(String fromId, FareSet fareSet) {
        fareSets.computeIfAbsent(fromId, x -> new FareSet(fromId)).combineWith(fareSet);
    }

    @Override
    public FareSet getFaresFrom(String fromId) {
        return fareSets.computeIfAbsent(fromId, x -> new FareSet(fromId));
    }

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return fareSets;
    }
}
