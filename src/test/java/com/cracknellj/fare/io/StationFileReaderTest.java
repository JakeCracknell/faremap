package com.cracknellj.fare.io;

import com.cracknellj.fare.objects.Station;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class StationFileReaderTest {

    @Test
    public void getStations() {
        List<Station> stations = StationFileReader.getStations();
        assertTrue(stations.size() > 2800);
    }
}