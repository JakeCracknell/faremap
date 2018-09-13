package com.cracknellj.fare.offline;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeFareDataProvider;
import com.cracknellj.fare.routefinding.DijkstraSplitTicketTask;
import com.cracknellj.fare.routefinding.OffPeakDijkstraSplitTicketTask;
import com.cracknellj.fare.routefinding.PeakTimeDijkstraSplitTicketTask;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Stream;

public class MakeFaresJson {
    private static final Logger LOG = LogManager.getLogger(MakeFaresJson.class);

    //Takes 2 hours to regenerate everything
    public static void main(String[] args) throws Exception {
        Map<String, Station> stations = Maps.uniqueIndex(StationFileReader.getStations(), s -> s.stationId);
        CompositeFareDataProvider fareDataProvider = CompositeFareDataProvider.load();
//
//        stations.parallelStream()
//                .filter(s -> s.crs != null && s.crs.startsWith("H"))
//                .map(s -> s.stationId)
        Stream.of("910GHADLYWD", "910GHATFILD")
                .forEach(station -> {
                    Stopwatch stopwatch = Stopwatch.createStarted();
                    LOG.info("Starting " + station);
                    FareSet splitTicketFareset = Stream.of(
                            new OffPeakDijkstraSplitTicketTask(stations, fareDataProvider, station),
                            new PeakTimeDijkstraSplitTicketTask(stations, fareDataProvider, station)
                            ).map(DijkstraSplitTicketTask::findCheapestRoutes)
                            .reduce(FareSet::combine).orElse(new FareSet(station));
                    splitTicketFareset.combineWith(fareDataProvider.getFaresFrom(station));
                    LOG.info(String.format("Completed %s in %d seconds", station, stopwatch.elapsed().getSeconds()));
                    writeFaresJson(splitTicketFareset);
                });


//        Set<String> existing = Files.list(Paths.get("web", "data", "fares"))
//                .map(p -> p.getFileName().toString().replace(".json", ""))
//                .collect(Collectors.toSet());
//        stations.parallelStream().filter(s -> !existing.contains(s.stationId))
//                .forEach(MakeFaresJson::writeFaresJson);

    }

    private static void writeFaresJson(FareSet fareSet) {
        try (Writer writer = Files.newBufferedWriter(Paths.get("web", "data", "fares", fareSet.fromId + ".json"))) {
            new Gson().toJson(fareSet, writer);
            LOG.info("Writing data for " + fareSet.fromId + " - DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
