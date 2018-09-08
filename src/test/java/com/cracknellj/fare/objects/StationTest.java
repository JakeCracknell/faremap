package com.cracknellj.fare.objects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class StationTest {
    @Test
    public void serialiseAndDeserialise() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Station station1 = new Station("940GZZDLABR", "Abbey Road", EnumSet.of(StationTag.DLR), 51.5319f, 0.003737f, null, 12345);
        String stationJson1 = gson.toJson(station1);
        Station station2 = gson.fromJson(stationJson1, Station.class);
        String stationJson2 = gson.toJson(station2);

        System.out.println(stationJson1);
        assertEquals(stationJson1, stationJson2);
    }
}
