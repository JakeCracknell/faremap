package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareSet;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class AtocDataProviderTest {
    @Test
    @Ignore
    public void getAllFareSets() throws Exception {
        while (true) {
            Map<String, FareSet> allFareSets = new AtocDataProvider().getAllFareSets();
            System.out.println(allFareSets.size());
        }

    }

}