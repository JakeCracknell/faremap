package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.StationTag;
import com.cracknellj.fare.provider.TFLDataProvider;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

public class RebuildTFLData {
    private static final Logger LOG = LogManager.getLogger(RebuildTFLData.class);

    private static TFLFareScraper tflFareScraper = new TFLFareScraper();
    private static List<String> stationIds;

    public static void main(String[] args) throws SQLException {
        stationIds = StationFileReader.getStations().stream()
                .filter(s -> s.tags.contains(StationTag.OYSTER))
                .map(s -> s.stationId)
                .sorted().collect(Collectors.toList());

        Map<String, FareSet> fareSetMap = new TFLDataProvider().getAllFareSets();
        List<Fare> faresToQuery = getMissingFaresToQuery(fareSetMap);
        List<Fare> faresToInsert = lookupFares(faresToQuery);
        faresToInsert.forEach(f -> fareSetMap.computeIfAbsent(f.fromId, (x) -> new FareSet(f.fromId)).add(f));

        writeFaresToFile(fareSetMap);
    }

    private static List<Fare> lookupFares(List<Fare> faresToQuery) {
        return faresToQuery.parallelStream()
                .map(f -> tflFareScraper.lookupFare(f.fromId, f.toId))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static List<Fare> getMissingFaresToQuery(Map<String, FareSet> fareSetMap) {
        return stationIds.stream().flatMap(fromStation -> {
            FareSet fareSet = fareSetMap.get(fromStation);
            Set<String> toStations = new HashSet<>(stationIds);
            toStations.remove(fromStation);
            toStations.removeAll(fareSet.fares.keySet());
            return toStations.stream().map(toStation -> new Fare(fromStation, toStation, null));
        }).collect(Collectors.toList());
    }

    private static void writeFaresToFile(Map<String, FareSet> fareSetMap) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(
                new GZIPOutputStream(new FileOutputStream("tfl2.json.gz"))))) {
            Gson gson = new Gson();
            gson.toJson(fareSetMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}