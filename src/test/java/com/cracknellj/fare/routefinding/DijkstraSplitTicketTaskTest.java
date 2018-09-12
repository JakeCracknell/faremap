package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import com.google.common.base.Stopwatch;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class DijkstraSplitTicketTaskTest {

    private static FareDataProvider fareDataProvider;
    private static Collection<Station> stations;

    @BeforeClass
    public static void setUp() throws Exception {
        fareDataProvider = CompositeFareDataProvider.load();
        stations = StationFileReader.getStations();
    }

    @Test //3200 ms
    public void performanceTestHAT() throws Exception {
        for (int i = 0; i < 5; i++) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            DijkstraSplitTicketTask dijkstraSplitTicketTask = new PeakTimeDijkstraSplitTicketTask(stations, fareDataProvider);
            FareSet cheapestRoutes = dijkstraSplitTicketTask.findCheapestRoutes("910GHATFILD");
            FareSet cheapestRoutes2 = dijkstraSplitTicketTask.findCheapestRoutes("910GWELHAMG");
            System.out.println(stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }
}