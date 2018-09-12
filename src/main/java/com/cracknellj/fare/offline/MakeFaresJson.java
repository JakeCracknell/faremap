package com.cracknellj.fare.offline;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeFareDataProvider;
import com.cracknellj.fare.routefinding.DijkstraSplitTicketTask;
import com.cracknellj.fare.routefinding.OffPeakDijkstraSplitTicketTask;
import com.cracknellj.fare.routefinding.PeakTimeDijkstraSplitTicketTask;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class MakeFaresJson {
    private static final Logger LOG = LogManager.getLogger(MakeFaresJson.class);

    //Takes 2 hours to regenerate everything
    public static void main(String[] args) throws Exception {
        List<Station> stations = StationFileReader.getStations();
        CompositeFareDataProvider fareDataProvider = CompositeFareDataProvider.load();

        stations.parallelStream()
                .filter(s -> "HAT".equals(s.crs))
                .map(s -> s.stationId)
                .forEach(station -> {
                    Stream.of(
                            new OffPeakDijkstraSplitTicketTask(stations, fareDataProvider),
                            new PeakTimeDijkstraSplitTicketTask(stations, fareDataProvider)
                    ).map(t -> t.findCheapestRoutes(station))
                            .forEach(fareSet -> fareDataProvider.add(station, fareSet));
                    writeFaresJson(fareDataProvider.getFaresFrom(station));
                });


//        Set<String> existing = Files.list(Paths.get("web", "data", "fares"))
//                .map(p -> p.getFileName().toString().replace(".json", ""))
//                .collect(Collectors.toSet());
//        stations.parallelStream().filter(s -> !existing.contains(s.stationId))
//                .forEach(MakeFaresJson::writeFaresJson);

    }

    private static void writeFaresJson(FareSet fareSet) {
        try (Writer writer = Files.newBufferedWriter(Paths.get("web", "data", "fares", fareSet.fromId + ".json"))) {
            LOG.info("Writing data for " + fareSet.fromId);
            new Gson().toJson(fareSet, writer);
            LOG.info("Writing data for " + fareSet.fromId + " - DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
