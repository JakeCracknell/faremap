package com.cracknellj.fare.objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Seems hacky, but speeds up Dijkstra 5-10x so it's staying
public class FareDetailCollection extends ArrayList<FareDetail> implements List<FareDetail> {
    private static final FareDetailCollection EMPTY_INSTANCE = new FareDetailCollection(0);
    private static final Comparator<FareDetail> FARE_DETAIL_COMPARATOR = Comparator.comparingInt(f -> f.price);

    public transient FareDetail cheapestOffpeak;
    public transient FareDetail cheapestPeak;

    public FareDetailCollection(int capacity) {
        super(capacity);
    }

    public FareDetailCollection() {
        super(10);
    }

    public static FareDetailCollection empty() {
        return EMPTY_INSTANCE;
    }

    public void calculateAndCacheCheapestFares() {
        cheapestOffpeak = this.stream().min(FARE_DETAIL_COMPARATOR).orElse(null);
        cheapestPeak = this.stream().filter(f -> !f.offPeakOnly).min(FARE_DETAIL_COMPARATOR).orElse(null);
    }

}
