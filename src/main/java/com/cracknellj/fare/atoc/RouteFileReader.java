package com.cracknellj.fare.atoc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RouteFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(RouteFileReader.class);

    private static final String FILE_EXTENSION = "RTE";
    private static final Pattern MULTI_WHITE_SPACE_REGEX_PATTERN = Pattern.compile("\\s+");

    public RouteFileReader() throws IOException {
        super(FILE_EXTENSION);
    }

    public Map<String, AtocRouteDetails> getAtocRoutes() throws IOException {
        Map<String, AtocRouteDetails> map = new HashMap<>();
        try (Stream<String> lineStream = getStreamOfLines()) {
            lineStream.forEach(line -> {
                switch (line.charAt(1)) {
                    case 'L':
                        //TODO: do we need to read these route/include/exclude locations records?
//                        char isExclOrIncl = line.charAt(25);
//                        if (isExclOrIncl == 'E') {
//                            String routeCode = line.substring(2, 7);
//                            AtocRouteDetails route = map.computeIfAbsent(routeCode, (x) -> new AtocRouteDetails());
//                            //Ignoring admin area code exlcusion. And any inclusions.
//                            String nlc = line.substring(19, 23);
//                            route.nlcExclusions.add(nlc);
//                        }
                        break;
                    case 'R':
                        String routeCode = line.substring(2, 7);
                        AtocRouteDetails route = map.computeIfAbsent(routeCode, (x) -> new AtocRouteDetails());
                        String longWrittenDescriptionOnTicket = cleanDescription(line.substring(47, 187));
                        String shortDescription = cleanDescription(line.substring(31, 47));
                        if (longWrittenDescriptionOnTicket.length() > shortDescription.length()) {
                            route.description = longWrittenDescriptionOnTicket;
                        } else {
                            route.description = shortDescription;
                        }
                }
            });
        }
        LOG.info(map.size() + " entries found");
        return map;
    }

    private String cleanDescription(String description) {
        description = description.trim();
        description = MULTI_WHITE_SPACE_REGEX_PATTERN.matcher(description).replaceAll(" ");
        return description;
    }
}
