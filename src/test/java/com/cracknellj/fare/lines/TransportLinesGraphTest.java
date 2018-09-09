package com.cracknellj.fare.lines;

import org.junit.Test;

import static org.junit.Assert.*;

public class TransportLinesGraphTest {
    @Test
    public void testName() throws Exception {
        TransportLinesGraph transportLinesGraph = new TransportLinesGraph();
        double distance = transportLinesGraph.getDistance("910GWCROYDN", "910GWADDON");
        assertEquals(1.6578970591491933, distance, 0.0001);
    }
}