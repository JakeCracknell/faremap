package com.cracknellj.fare.offline;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.io.StationFileWriter;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.objects.StationTag;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StationUsageParser {
    private static final Logger LOG = LogManager.getLogger(StationUsageParser.class);

    private static List<Station> stations;

    public static void main(String[] args) throws Exception {
        stations = StationFileReader.getStations();
        Files.lines(Paths.get("stationusage", "tube.csv")).forEach(StationUsageParser::readTubeStation);
        Files.lines(Paths.get("stationusage", "dlr.csv")).forEach(StationUsageParser::readDlrStation);
        Files.lines(Paths.get("stationusage", "nr.csv")).forEach(StationUsageParser::readNationalRailStation);
        LOG.info("No yearly usage figures saved for: ");
        stations.stream().filter(s -> s.yearlyUsage == 0).forEach(LOG::info);
        StationFileWriter.writeStations(stations);
    }

    private static void readTubeStation(String line) {
        String[] split = line.split(",");
        Station station = stations.stream()
                .filter(m -> m.tags.contains(StationTag.TUBE))
                .min(Comparator.comparingInt(s -> LevenshteinDistance.getDefaultInstance().apply(s.stationName, split[0])))
                .orElseThrow(() -> new RuntimeException(""));
        if (station.stationName.equals(split[0])) {
            station.yearlyUsage = (int) (Double.parseDouble(split[1]) * 1000000);
        } else {
            LOG.info(line + " ??? " + station);
        }
    }

    private static void readDlrStation(String line) {
        String[] split = line.split(",");
        Station station = stations.stream()
                .filter(m -> m.tags.contains(StationTag.DLR))
                .min(Comparator.comparingInt(s -> LevenshteinDistance.getDefaultInstance().apply(s.stationName, split[0])))
                .orElseThrow(() -> new RuntimeException(""));
        if (station.stationName.equals(split[0])) {
            station.yearlyUsage = Integer.parseInt(split[1]);
        } else {
            LOG.info(line + " ??? " + station);
        }
    }

    private static void readNationalRailStation(String line) {
        String[] split = line.split(",");
        Optional<Station> stationOptional = stations.stream().filter(s -> split[0].equals(s.crs)).findFirst();
        if (stationOptional.isPresent()) {
            stationOptional.get().yearlyUsage = Integer.parseInt(split[1]);
        } else {
            LOG.info(line + " ??? " + stationOptional);
        }

    }
}
