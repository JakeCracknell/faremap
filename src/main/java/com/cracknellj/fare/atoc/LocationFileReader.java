package com.cracknellj.fare.atoc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

//Page 62
public class LocationFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(LocationFileReader.class);

    public static final String FILE_NAME = "RJFAF499.LOC";

    public Map<String, String> getNLCToCRSMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        try (Stream<String> lineStream = getStreamOfLines(FILE_NAME)) {
            lineStream.forEach(line -> {
                switch (line.charAt(1)) {
                    case 'L':
                        String adminAreaCode = line.substring(33, 36);
                        if (adminAreaCode.equals("70 ")) {
                            String nlc = line.substring(36, 40);
                            String crs = line.substring(56, 59);
                            if (crs.charAt(0) != ' ') {
                                map.put(nlc, crs);
                            }
                        }
                }
            });
        }
        LOG.info(map.size() + " entries found");
        return map;
    }

}
