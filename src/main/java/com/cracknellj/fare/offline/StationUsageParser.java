package com.cracknellj.fare.offline;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.Station;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StationUsageParser {
    private static final Logger LOG = LogManager.getLogger(StationUsageParser.class);

    private static List<Station> stations;

    public static void main(String[] args) throws Exception {
        stations = StationFileReader.getStations();
        Files.lines(Paths.get("stationusage", "tfl.csv")).forEach(StationUsageParser::readTflStation);
        Files.lines(Paths.get("stationusage", "nr.csv")).forEach(StationUsageParser::readNationalRailStation);

    }

    private static void readTflStation(String line) {
        String[] split = line.split(",");
        List<Station> collect = stations.stream().sorted(
                Comparator.comparingInt(s -> LevenshteinDistance.getDefaultInstance().apply(s.stationName, split[0]))
        ).collect(Collectors.toList());
        LOG.info(line + " -> " + collect.get(0));


    }

    private static void readNationalRailStation(String line) {
        String[] split = line.split(",");
        Optional<Station> stationOptional = stations.stream().filter(s -> split[0].equals(s.crs)).findFirst();
        LOG.info(line + " -> " + stationOptional);
        if (stationOptional.isPresent()) {
            stationOptional.get();
        }

    }
}
