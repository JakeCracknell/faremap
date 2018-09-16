package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class DijkstraSplitTicketTaskTest {
    private static FareDataProvider fareDataProvider;
    private static Map<String, Station> stations;

    @BeforeClass
    public static void setUp() throws Exception {
        fareDataProvider = CompositeFareDataProvider.load();
        stations = Maps.uniqueIndex(StationFileReader.getStations(), s -> s.stationId);
    }

    @Test //3200 ms
    public void performanceTestHAT() throws Exception {
        for (int i = 0; i < 5; i++) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            DijkstraSplitTicketTask dijkstraSplitTicketTask = new PeakTimeDijkstraSplitTicketTask(stations, fareDataProvider, "910GHATFILD");
            dijkstraSplitTicketTask.findCheapestRoutes();
            System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

}