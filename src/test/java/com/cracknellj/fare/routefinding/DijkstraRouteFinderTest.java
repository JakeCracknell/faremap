package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeSingletonFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import com.google.common.base.Stopwatch;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class DijkstraRouteFinderTest {

    private static FareDataProvider fareDataProvider;
    private static Collection<Station> stations;

    @BeforeClass
    public static void setUp() throws Exception {
        fareDataProvider = CompositeSingletonFareDataProvider.getInstance();
        stations = StationFileReader.getStations();
    }

    @Test //3200 ms
    public void performanceTestHAT() throws Exception {
        for (int i = 0; i < 5; i++) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            DijkstraRouteFinder dijkstraRouteFinder = new DijkstraRouteFinder(stations, fareDataProvider, false);
            FareSet cheapestRoutes = dijkstraRouteFinder.findCheapestRoutes("910GHATFILD");
            FareSet cheapestRoutes2 = dijkstraRouteFinder.findCheapestRoutes("910GWELHAMG");
            System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }
}