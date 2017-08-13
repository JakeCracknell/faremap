package com.cracknellj.fare.atoc;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class LocationFileReaderTest {
    @Test
    public void getNLSToCRSMap() throws Exception {
        Map<String, String> nlsToCRSMap = new LocationFileReader().getNLSToCRSMap();
        assertEquals("HAT", nlsToCRSMap.get("6070"));
    }

}