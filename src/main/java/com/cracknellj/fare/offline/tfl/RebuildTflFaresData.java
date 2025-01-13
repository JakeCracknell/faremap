package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.StationTag;
import com.cracknellj.fare.provider.TFLDataProvider;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

public class RebuildTflFaresData {
    private static final boolean REWRITE_ALL = false;
    private static final Logger LOG = LogManager.getLogger(RebuildTflFaresData.class);

    private static final TflFareScraper tflFareScraper = new TflFareScraper();
    private static List<String> stationIds;

    public static void main(String[] args) {
        stationIds = StationFileReader.getStations().stream()
                .filter(s -> s.tags.contains(StationTag.TFLFARE))
                .map(s -> s.stationId)
                .sorted().collect(Collectors.toList());

        Map<String, FareSet> fareSetMap = new TFLDataProvider().getAllFareSets();
        if (REWRITE_ALL) {
            LOG.info("Clearing all fares, as REWRITE_ALL is set");
            fareSetMap.clear();
        }

        List<Fare> faresToQuery = getMissingFaresToQuery(fareSetMap);
        LOG.info("There are " + faresToQuery.size() + " fares to query");
        LOG.info("In particular, the most missing fares are from: " + faresToQuery.stream()
                .collect(Collectors.groupingBy(f -> f.fromId, Collectors.counting()))
                .entrySet().stream().sorted(Comparator.comparing(e -> -e.getValue())).limit(50)
                .collect(Collectors.toList()));
        List<Fare> faresToInsert = lookupFares(faresToQuery);
        faresToInsert.forEach(f -> fareSetMap.computeIfAbsent(f.fromId, (x) -> new FareSet(f.fromId)).add(f));

        writeFaresToFile(fareSetMap);
    }

    private static List<Fare> lookupFares(List<Fare> faresToQuery) {
        AtomicInteger completedCount = new AtomicInteger(0);
        Stopwatch stopwatch = Stopwatch.createStarted();
        return faresToQuery.parallelStream()
                .map(f -> {
                    List<Fare> fares = tflFareScraper.lookupFare(f.fromId, f.toId);
                    logProgress(completedCount.incrementAndGet(), faresToQuery.size(), stopwatch);
                    return fares;
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private static void logProgress(int completedCount, int totalCount, Stopwatch stopwatch) {
        double percComplete = (double) completedCount / totalCount;
        Duration timeLeft = Duration.ofMillis((long) (stopwatch.elapsed().toMillis() * (1.0 / percComplete - 1)));
        LOG.info(String.format("%d/%d completed, %.3f%%, %s elapsed, %s left, ETA %s", completedCount, totalCount,
                percComplete * 100, stopwatch.elapsed(), timeLeft, LocalDateTime.now().plus(timeLeft)));
    }

    private static List<Fare> getMissingFaresToQuery(Map<String, FareSet> fareSetMap) {
        return stationIds.stream().flatMap(fromStation -> {
            FareSet fareSet = fareSetMap.getOrDefault(fromStation, new FareSet(fromStation));
            Set<String> toStations = new HashSet<>(stationIds);
            toStations.remove(fromStation);
            toStations.removeAll(fareSet.fares.keySet());
            return toStations.stream().map(toStation -> new Fare(fromStation, toStation, null));
        }).collect(Collectors.toList());
    }

    private static void writeFaresToFile(Map<String, FareSet> fareSetMap) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(
                new GZIPOutputStream(new FileOutputStream(System.currentTimeMillis() + "-tfl.json.gz"))))) {
            Gson gson = new Gson();
            gson.toJson(fareSetMap, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}