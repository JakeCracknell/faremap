package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.objects.Fare;
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

}