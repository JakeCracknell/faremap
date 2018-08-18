package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.*;
import com.cracknellj.fare.provider.FareDataProvider;
import com.cracknellj.fare.provider.SplitTicketDataProvider;
import com.cracknellj.fare.provider.TFLDataProvider;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.*;

public class GenerateTFLSplitTicketSpreadsheet {

    public static void main(String[] args) throws Exception {
        Map<String, Station> tflStations = StationFileReader.getStations().stream()
                .filter(s -> s.tags.contains(StationTag.OYSTER)).collect(toMap(s -> s.stationId, Function.identity()));
        FareDataProvider tflFaresProvider = new TFLDataProvider();

        SplitTicketDataProvider provider = new SplitTicketDataProvider();
        List<Fare> splitTicketFares = tflStations.keySet().parallelStream()
                .flatMap(fromId -> provider.getFaresFrom(fromId).toFareList().stream())
                .filter(f -> tflStations.containsKey(f.toId)).collect(Collectors.toList());

        Map<String, FareSet> tflProperFares = tflFaresProvider.getAllFareSets();
        List<String> lines = new ArrayList<>();
        for (Fare splitTicketFare : splitTicketFares) {
            FareSet fareSet = tflProperFares.get(splitTicketFare.fromId);
            if (fareSet != null) {
                List<FareDetail> tflFares = fareSet.fares.getOrDefault(splitTicketFare.toId, emptyList());
                tflFares.stream().sorted(Comparator.comparingInt(f -> f.price)).findFirst().ifPresent(properFare -> {
                    String line = Stream.of(tflStations.get(
                            splitTicketFare.fromId).stationName, tflStations.get(splitTicketFare.toId).stationName,
                            properFare.price, splitTicketFare.fareDetail.price,
                            splitTicketFare.fareDetail.routeDescription.replace(",", ""))
                            .map(Object::toString).collect(joining(","));
                    lines.add(line);
                });
            }
        }
        Collections.sort(lines);
        Files.write(Paths.get("tfl_split_tickets.csv"), lines);
    }
}
