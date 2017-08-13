package com.cracknellj.fare.atoc;

import com.cracknellj.fare.ws.objects.FareDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

//Page 62
public class FareFlowFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(FareFlowFileReader.class);

    private static final BigDecimal FARE_DIVISOR = BigDecimal.valueOf(100);
    public static final String FILE_NAME = "RJFAF499.FFL";

    public List<AtocFare> getFaresList() throws IOException {
        List<AtocFare> fares = new ArrayList<>();
        Map<String, String> flowIdToOriginDestinationMap = new HashMap<>();
        try (Stream<String> lineStream = getStreamOfLines(FILE_NAME)) {
            lineStream.forEach(line -> {
                switch (line.charAt(1)) {
                    case 'F':
                        String flowId = line.substring(42, 49);
                        String originDestination = line.substring(2, 10);
                        flowIdToOriginDestinationMap.put(flowId, originDestination);
                        boolean reversible = line.charAt(19) == 'R';
                        if (reversible) {
                            flowIdToOriginDestinationMap.put(flowId, originDestination.substring(0, 4) + originDestination.substring(4, 8));
                        }
                    case 'T':
                        String restriction = line.substring(20, 22);
                        if (restriction.charAt(0) == ' ') {
                            String ticketCode = line.substring(9, 12);
                            Boolean offPeakOnly = isOffPeakTicketCode(ticketCode);
                            if (offPeakOnly != null) {
                                String tFlowId = line.substring(2, 9);
                                String farePence = line.substring(12, 20);
                                BigDecimal farePrice = BigDecimal.valueOf(Integer.parseInt(farePence)).divide(FARE_DIVISOR, 2, BigDecimal.ROUND_UNNECESSARY);
                                String tOriginDestination = flowIdToOriginDestinationMap.get(tFlowId);
                                String nlsFrom = tOriginDestination.substring(0, 4);
                                String nlsTo = tOriginDestination.substring(4, 8);
                                String fareDescription = getFareDescriptionForTicketCode(ticketCode);
                                FareDetail fareDetail = new FareDetail(farePrice, offPeakOnly, fareDescription, !offPeakOnly, "NR");
                                fares.add(new AtocFare(nlsFrom, nlsTo, fareDetail));
                            }
                        }

                }
            });
        }
        LOG.info(fares.size() + " entries found");
        return fares;
    }

    private Boolean isOffPeakTicketCode(String ticketCode) {
        switch (ticketCode) {
            case "SDS": //Anytime Day Single
                return Boolean.FALSE;
            case "CDS": //Off-Peak Day Single
                return Boolean.TRUE;
            default:
                return null;
        }
    }

    private String getFareDescriptionForTicketCode(String ticketCode) {
        switch (ticketCode) {
            case "SDS": //Anytime Day Single
                return "Anytime Day Single";
            case "CDS": //Off-Peak Day Single
                return "Off-Peak Day Single";
            default:
                return null;
        }
    }

}
