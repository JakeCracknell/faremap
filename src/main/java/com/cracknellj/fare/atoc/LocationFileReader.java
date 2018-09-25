package com.cracknellj.fare.atoc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

//Page 62
public class LocationFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(LocationFileReader.class);

    private static final String FILE_EXTENSION = "LOC";
    private static final Pattern LONDON_ZONE_GROUP_PATTERN = Pattern.compile("ZONE U.+LONDN");

    public LocationFileReader() throws IOException {
        super(FILE_EXTENSION);
    }

    //RL706070031122999020120151203201370 6070HATFIELD (HERTS)HAT00000     6070  04SE      13HATFIELD (HERTS)
    //              HATFIELD (HERTS)HATFIELD (HERTS)                                            HATFIELD (HERTS)
    //                                0 NNNNNN0009002S5010092000000
    public Map<String, String> getNLCToCRSMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        try (Stream<String> lineStream = getStreamOfLines()) {
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

    //London zonal logic largely copied from BRFares/librailfare locations.c

    //RG7010720311229990104200001042000LONDON TERMINALS
    public Map<String, Set<String>> getStationGroups() throws IOException {
        try (Stream<String> lineStream = getStreamOfLines()) {
            Map<String, Set<String>> map = new HashMap<>();
            Map<Integer, Set<String>> crssByLondonZone = new HashMap<>();
            Map<String, String> nlcsToLondonZoneString = new HashMap<>();
            lineStream.forEach(line -> {
                switch (line.charAt(1)) {
                    case 'L':
                        String adminAreaCode = line.substring(33, 36);
                        if (adminAreaCode.equals("70 ")) {
                            String nlc = line.substring(36, 40);
                            String crs = line.substring(56, 59);
                            String fareGroup = line.substring(69, 73);
                            if (!nlc.equals(fareGroup) && crs.charAt(0) != ' ') {
                                map.computeIfAbsent(fareGroup, x -> new HashSet<>()).add(crs);
                            }
                            char londonZone = line.charAt(83);
                            if (Character.isDigit(londonZone)) {
                                String stationName = line.substring(87, 103).trim();
                                if (isEligibleForZonalTicket(stationName)) {
                                    crssByLondonZone.computeIfAbsent(Character.getNumericValue(londonZone), x -> new HashSet<>()).add(crs);
                                }
                            }
                            String description = line.substring(40, 56);
                            if (LONDON_ZONE_GROUP_PATTERN.matcher(description).matches()) {
                                String londonZoneString = line.substring(46, 50); // '1245' or 'U2* ' etc
                                nlcsToLondonZoneString.put(nlc, londonZoneString);
                            }
                        }
                        break;
                    case 'M':
                        String uic = line.substring(4, 8);
                        String crs = line.substring(24, 27);
                        map.computeIfAbsent(uic, x -> new HashSet<>()).add(crs);
                }
            });
            removeInvalidStationGroups(map);

            nlcsToLondonZoneString.forEach((nlc, zoneString) -> {
                String cleanedZoneList = zoneString.replaceAll("[^\\d]", "");
                IntStream.rangeClosed(Character.getNumericValue(cleanedZoneList.charAt(0)),
                        Character.getNumericValue(cleanedZoneList.charAt(cleanedZoneList.length() - 1)))
                        .forEach(zoneNumber -> {
                            map.computeIfAbsent(nlc, x -> new HashSet<>()).addAll(crssByLondonZone.get(zoneNumber));
                        });
            });

            LOG.info(map.size() + " station groups found");
            return map;
        }
    }

    //TODO check for Z?? crs only? HAMMERSMTH M/UND ??? Some dont have any like SOUTHFIELDS or NORTH GREENWICH
    private boolean isEligibleForZonalTicket(String stationName) {
        return stationName.endsWith(" DLR") || stationName.endsWith(" UND") ||
                stationName.endsWith(" UNDERGD") || stationName.endsWith(" LT") || stationName.endsWith(" LRT");
    }

    //Tempted to remove this, as bus routes will still slip through. e.g. HAT->LUT
    //90% of these groups are things like J944 SWINDON+BUS. We want ones like 1072 LONDON TERMINALS
    private void removeInvalidStationGroups(Map<String, Set<String>> map) {
        Set<String> invalidGroups = map.keySet().stream().filter(s -> !s.matches("\\d+")).collect(Collectors.toSet());
        invalidGroups.remove("H584"); //HEATHROW RAIL
        map.keySet().removeAll(invalidGroups);
    }

}
