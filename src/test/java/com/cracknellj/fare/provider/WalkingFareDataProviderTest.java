package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareSet;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class WalkingFareDataProviderTest {

    @Test
    public void testGetAllFareSets() throws Exception {
        Map<String, FareSet> allFareSets = new WalkingFareDataProvider().getAllFareSets();
        System.out.println(allFareSets.size());
    }
}