package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeSingletonFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import jersey.repackaged.com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class DjikstraRouteFinderTest {

    private static FareDataProvider fareDataProvider;
    private static Collection<Station> stations;

    @BeforeClass
    public static void setUp() throws Exception {
        fareDataProvider = CompositeSingletonFareDataProvider.getInstance();
        stations = new StationDAO().getStations();
    }

    @Test
    public void performanceTestHAT() throws Exception {
        for (int i = 0; i < 5; i++) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            DjikstraRouteFinder djikstraRouteFinder = new DjikstraRouteFinder(stations, fareDataProvider);
            djikstraRouteFinder.findBestRoute("910GHATFILD", "910GDUNDETB");
            System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }
}