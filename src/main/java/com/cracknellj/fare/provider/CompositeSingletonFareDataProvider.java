package com.cracknellj.fare.provider;

import com.cracknellj.fare.atoc.AtocDataReader;
import com.cracknellj.fare.objects.FareSet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class CompositeSingletonFareDataProvider implements FareDataProvider {
    private final Map<String, FareSet> fareSets;

    private static FareDataProvider ourInstance = new CompositeSingletonFareDataProvider();

    public CompositeSingletonFareDataProvider() {
        fareSets = Stream.of(new AtocDataProvider(), new TFLDataProvider())
                .map(FareDataProvider::getAllFareSets).reduce(FareSet::combine)
                .orElseThrow(() -> new RuntimeException("Java y u do dis?"));
    }

    public synchronized static FareDataProvider getInstance() {
        return ourInstance;
    }

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return fareSets;
    }
}
