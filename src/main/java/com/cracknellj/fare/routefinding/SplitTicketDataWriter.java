package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeSingletonFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import static java.util.stream.Collectors.toMap;

public class SplitTicketDataWriter {
    public static void main(String[] args) throws Exception {
        List<Station> stations = StationFileReader.getStations();
        FareDataProvider fareDataProvider = CompositeSingletonFareDataProvider.getInstance();
        DijkstraRouteFinder dijkstraRouteFinder = new DijkstraRouteFinder(stations, fareDataProvider);
        stations.parallelStream()
                .map(s -> dijkstraRouteFinder.findCheapestRoutes(s.stationId)).forEach(fareSet -> {
                    try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(
                            new GZIPOutputStream(new FileOutputStream("splitticket\\" + fareSet.fromId + ".json.gz"))))) {
                        Gson gson = new Gson();
                        gson.toJson(fareSet, writer);
                    } catch (Exception ignored) {
                    }
                });

    }
}
