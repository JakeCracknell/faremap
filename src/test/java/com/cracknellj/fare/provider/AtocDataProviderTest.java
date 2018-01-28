package com.cracknellj.fare.provider;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import jersey.repackaged.com.google.common.base.Stopwatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class AtocDataProviderTest {
    @Test
    @Ignore
    public void getAllFareSets() throws Exception {
        while (true) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Map<String, FareSet> allFareSets = new AtocDataProvider().getAllFareSets();
            System.out.println("Time elapsed: " + stopwatch.elapsed(TimeUnit.MILLISECONDS) + ", FareSet size: " + allFareSets.size());
        }

    }

    @Test
    public void analyseJourneyValue() throws Exception {
        FareDataProvider fareDataProvider = CompositeSingletonFareDataProvider.getInstance();
        List<Station> stations = StationFileReader.getStations();

        double cheapestPricePerKm = Double.MAX_VALUE;
        for (Station stationFrom : stations) {
            for (Station stationTo : stations) {
                double distanceKm = Haversine.distance(stationFrom.latitude, stationFrom.longitude,
                        stationFrom.latitude, stationTo.longitude);
                Optional<FareDetail> fareDetail = fareDataProvider
                        .getFares(stationFrom.stationId, stationTo.stationId).stream()
                        .sorted(Comparator.comparingInt(f -> f.price)).findFirst();
                if (fareDetail.isPresent()) {
                    double pricePerKm = (fareDetail.get().price / 100) / distanceKm;
                    if (pricePerKm < cheapestPricePerKm && pricePerKm != 0 && !fareDetail.get().isTFL) {
                        cheapestPricePerKm = pricePerKm;
                        System.out.printf("£%.4f/km,%s,%s,%s%n", cheapestPricePerKm, stationFrom, stationTo, fareDetail.get());
                    }
                }
            }

        }

    }
}