package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.io.StationFileWriter;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.objects.StationTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class IdentifyTflPaygStations {
    private static final Logger LOG = LogManager.getLogger(IdentifyTflPaygStations.class);
    private static final TflFareScraper tflFareScraper = new TflFareScraper();
    private static final String TARGET_DESTINATION_STATION_ID = "940GZZLUOXC"; // Oxford Circus
    private static List<Station> stations;

    public static void main(String[] args) throws Exception {
        stations = StationFileReader.getStations();
        stations.parallelStream().forEach(station -> {
            if (!station.tags.contains(StationTag.TFLFARE)) {
                if (isTflPaygStation(station)) {
                    LOG.info("Adding TFLFARE tag to " + station);
                    station.tags.add(StationTag.TFLFARE);
                }
            }
        });
        StationFileWriter.writeStations(stations);
    }

    private static boolean isTflPaygStation(Station station) {
        List<Fare> fares = tflFareScraper.lookupFare(station.stationId, TARGET_DESTINATION_STATION_ID);
        return !fares.isEmpty();
    }

}
