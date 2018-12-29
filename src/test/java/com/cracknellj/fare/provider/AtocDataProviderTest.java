package com.cracknellj.fare.provider;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.google.common.base.Stopwatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.TimeUnit;

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
        FareDataProvider fareDataProvider = CompositeFareDataProvider.load();
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

    @Test
    public void findUnfairReverseJourneys() throws Exception {
        FareDataProvider fareDataProvider = CompositeFareDataProvider.load();
        List<Station> stations = StationFileReader.getStations();

        int biggestFareDifference = 0;
        for (Station stationFrom : stations) {
            for (Station stationTo : stations) {
                FareDetail fareDetail1 = fareDataProvider
                        .getFares(stationFrom.stationId, stationTo.stationId).cheapestPeak;
                FareDetail fareDetail2 = fareDataProvider
                        .getFares(stationTo.stationId, stationFrom.stationId).cheapestPeak;
                if (fareDetail1 != null && fareDetail2 != null && fareDetail1.price != fareDetail2.price) {
                    int difference = Math.abs(fareDetail1.price - fareDetail2.price);
                    if (difference > biggestFareDifference) {
                        biggestFareDifference = difference;
                        System.out.printf("%dp,%s,%s%n", difference, stationFrom, stationTo);
                    }
                }
            }

        }

    }
}