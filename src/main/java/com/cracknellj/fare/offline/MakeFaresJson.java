package com.cracknellj.fare.offline;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeSingletonFareDataProvider;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MakeFaresJson {
    private static final Logger LOG = LogManager.getLogger(MakeFaresJson.class);

    //Takes 2 hours to regenerate everything
    public static void main(String[] args) throws Exception {
        List<Station> stations = StationFileReader.getStations();
        Set<String> existing = Files.list(Paths.get("web", "data", "fares"))
                .map(p -> p.getFileName().toString().replace(".json", ""))
                .collect(Collectors.toSet());
//        stations.parallelStream().filter(s -> !existing.contains(s.stationId))
//                .forEach(MakeFaresJson::writeFaresJson);
        stations.parallelStream().filter(s -> "HAT".equals(s.crs))
                .forEach(MakeFaresJson::writeFaresJson);
    }

    private static void writeFaresJson(Station station) {
        FareSet fareSet = CompositeSingletonFareDataProvider.getInstance().getFaresFrom(station.stationId);
        try (Writer writer = Files.newBufferedWriter(Paths.get("web", "data", "fares", station.stationId + ".json"))) {
            LOG.info("Writing data for " + station);
            new Gson().toJson(fareSet, writer);
            LOG.info("Writing data for " + station + " - DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
