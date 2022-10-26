package com.cracknellj.fare.offline;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeFareDataProvider;
import com.cracknellj.fare.routefinding.DijkstraSplitTicketTask;
import com.cracknellj.fare.routefinding.OffPeakDijkstraSplitTicketTask;
import com.cracknellj.fare.routefinding.PeakTimeDijkstraSplitTicketTask;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

public class MakeFaresJson {
    private static final Logger LOG = LogManager.getLogger(MakeFaresJson.class);
    public static final Gson GSON = new Gson();

    //Takes 30 mins to regenerate everything
    public static void main(String[] args) throws Exception {
        Map<String, Station> stations = StationFileReader.getStationsAsMap();
        CompositeFareDataProvider fareDataProvider = CompositeFareDataProvider.load();

        Set<String> existing = Files.list(Paths.get("web", "data", "fares"))
                .map(p -> p.getFileName().toString().replace(".json.gz", ""))
                .collect(Collectors.toSet());

        doWithTwiceAsManyThreads(() -> stations.values().parallelStream()
                //.filter(s -> Arrays.asList("HAT","PBR","HMC","BNS", "KWG","RMD","SAC","TED","PUT","HDW","BPK").contains(s.crs))
                .map(s -> s.stationId)
                .forEach(stationId -> {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    logTime(stationId, "starting", stopwatch);
                    FareSet splitTicketFareset = Stream.of(
                            new OffPeakDijkstraSplitTicketTask(stations, fareDataProvider, stationId),
                            new PeakTimeDijkstraSplitTicketTask(stations, fareDataProvider, stationId)
                    ).map(DijkstraSplitTicketTask::findCheapestRoutes)
                            .reduce(FareSet::combine).orElse(new FareSet(stationId));
                    splitTicketFareset.combineWith(fareDataProvider.getFaresFrom(stationId));
                    logTime(stationId, "calculation complete", stopwatch);
                    writeFaresJson(splitTicketFareset);
                    logTime(stationId, "json file written", stopwatch);
                }));
    }

    private static void logTime(String stationId, String stepComplete, Stopwatch stopwatch) {
        LOG.info(String.format("%s - %s - %.2f seconds", stationId, stepComplete, stopwatch.elapsed().toMillis() / 1000.0));
    }

    private static void doWithTwiceAsManyThreads(Runnable task) throws InterruptedException, ExecutionException {
        new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2).submit(task).get();
    }

    private static void writeFaresJson(FareSet fareSet) {
        Path path = Paths.get("web", "data", "fares", fareSet.fromId + ".json.gz");
        try (OutputStreamWriter writer = new OutputStreamWriter(new BufferedOutputStream(
                new GZIPOutputStream(Files.newOutputStream(path.toFile().toPath()))))) {
            Gson gson = new Gson();
            gson.toJson(fareSet, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
