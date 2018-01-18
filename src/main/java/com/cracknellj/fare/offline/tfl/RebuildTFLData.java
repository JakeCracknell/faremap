package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareSet;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

import static java.util.stream.Collectors.toMap;

public class RebuildTFLData {
    private static final Logger LOG = LogManager.getLogger(RebuildTFLData.class);

    private static TFLFareScraper tflFareScraper = new TFLFareScraper();
    private static List<String> stationIds;

    public static void main(String[] args) throws SQLException {
        stationIds = StationFileReader.getStations().stream()
                .filter(s -> s.oysterAccepted)
                .map(s -> s.stationId)
                .sorted().collect(Collectors.toList());

        Map<String, FareSet> fareSetMap = stationIds.stream()
                .map(RebuildTFLData::createFareSet)
                .collect(toMap(f -> f.fromId, Function.identity()));

        try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(
                new GZIPOutputStream(new FileOutputStream("tfl2.json.gz"))))) {
            Gson gson = new Gson();
            gson.toJson(fareSetMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static FareSet createFareSet(String fromId) {
        LOG.info(String.format("Starting performing fare lookups from %s (%d/%d)",
                fromId, stationIds.indexOf(fromId) + 1, stationIds.size()));
        FareSet fareSet = new FareSet(fromId);
        List<Fare> fares = stationIds.parallelStream()
                .flatMap(toId -> tflFareScraper.lookupFare(fromId, toId).stream())
                .collect(Collectors.toList());
        fares.forEach(fareSet::add);
        return fareSet;
    }

}