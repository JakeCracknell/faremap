package com.cracknellj.fare.atoc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public class StationClusterFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(StationClusterFileReader.class);

    public static final String FILE_NAME = "RJFAF499.FSC";

    public Map<String, List<String>> getStationClusters() throws IOException {
        try (Stream<String> lineStream = getStreamOfLines(FILE_NAME)) {
            Map<String, List<String>> map = lineStream.filter(l -> !l.startsWith("/"))
                    .collect(groupingBy(this::getClusterID, mapping(this::getLocationNlc, toList())));
            LOG.info(map.size() + " clusters found");
            return map;
        }
    }

    private String getClusterID(String l) {
        return l.substring(1, 5);
    }

    private String getLocationNlc(String l) {
        return l.substring(5, 9);
    }

}
