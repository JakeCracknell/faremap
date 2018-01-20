package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.objects.Fare;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TFLFareScraperTest {
    @Test
    public void lookupFare() throws Exception {
        TFLFareScraper tflFareScraper = new TFLFareScraper();
        List<Fare> fares = tflFareScraper.lookupFare("910GKEWGRDN", "910GHADLYWD");
        System.out.println(fares);
        assertTrue(fares.size() >= 2);
    }

    @Ignore("Will take a few minutes to run")
    @Test
    public void lookupFare_Missing() throws Exception {
        TFLFareScraper tflFareScraper = new TFLFareScraper();
        List<Fare> fares = tflFareScraper.lookupFare("london", "dallas");
        assertTrue(fares.isEmpty());
    }

}