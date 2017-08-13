package com.cracknellj.fare.atoc;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class LocationFileReaderTest {
    @Test
    public void getNLCToCRSMap() throws Exception {
        Map<String, String> nlcToCRSMap = new LocationFileReader().getNLCToCRSMap();
        assertEquals("HAT", nlcToCRSMap.get("6070"));
    }

}