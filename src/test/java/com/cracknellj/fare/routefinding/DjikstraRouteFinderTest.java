package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeSingletonFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import jersey.repackaged.com.google.common.base.Stopwatch;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class DjikstraRouteFinderTest {

    private static FareDataProvider fareDataProvider;
    private static Collection<Station> stations;

    @BeforeClass
    public static void setUp() throws Exception {
        fareDataProvider = CompositeSingletonFareDataProvider.getInstance();
        stations = new StationDAO().getStations();//.stream().filter(s -> s.stationName.startsWith("H")).collect(Collectors.toList());
    }

    @Test
    public void performanceTestHAT() throws Exception {
        for (int i = 0; i < 5; i++) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            DjikstraRouteFinder djikstraRouteFinder = new DjikstraRouteFinder(stations, fareDataProvider);
            FareSet cheapestRoutes = djikstraRouteFinder.findCheapestRoutes("910GHATFILD");
            System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }
}