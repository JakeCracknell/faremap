package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareSet;
import jersey.repackaged.com.google.common.base.Stopwatch;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;
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

}