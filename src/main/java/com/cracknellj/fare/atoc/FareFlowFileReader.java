package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.AtocTicketCode;
import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareDetail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Stream;

public class FareFlowFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(FareFlowFileReader.class);

    private static final BigDecimal FARE_DIVISOR = BigDecimal.valueOf(100);
    public static final String FILE_NAME = "RJFAF499.FFL";

    public List<Fare> getFaresList() throws IOException {
        Map<String, AtocTicketCode> ticketCodes = new TicketTypeFileReader().getTicketCodes();
        List<Fare> fares = new ArrayList<>();
        Map<String, String> flowIdToOriginDestinationMap = new HashMap<>();
        Set<String> reversibleFlowIds = new HashSet<>();
        try (Stream<String> lineStream = getStreamOfLines(FILE_NAME)) {
            lineStream.forEach(line -> {
                switch (line.charAt(1)) {
                    case 'F':
                        String flowId = line.substring(42, 49);
                        String originDestination = line.substring(2, 10);
                        flowIdToOriginDestinationMap.put(flowId, originDestination);
                        boolean reversible = line.charAt(19) == 'R';
                        if (reversible) {
                            reversibleFlowIds.add(flowId);
                        }
                    case 'T':
                        String restriction = line.substring(20, 22);
                        if (restriction.charAt(0) == ' ') {
                            String ticketCodeString = line.substring(9, 12);
                            if (ticketCodes.containsKey(ticketCodeString)) {
                                AtocTicketCode ticketCode = ticketCodes.get(ticketCodeString);
                                String tFlowId = line.substring(2, 9);
                                String farePence = line.substring(12, 20);
                                BigDecimal farePrice = BigDecimal.valueOf(Integer.parseInt(farePence)).divide(FARE_DIVISOR, 2, BigDecimal.ROUND_UNNECESSARY);
                                String tOriginDestination = flowIdToOriginDestinationMap.get(tFlowId);
                                String nlcFrom = tOriginDestination.substring(0, 4);
                                String nlcTo = tOriginDestination.substring(4, 8);
                                FareDetail fareDetail = new FareDetail(farePrice, ticketCode.isOffPeak(), ticketCode.description, ticketCode.isDefaultFare(), "NR", false);
                                fares.add(new Fare(nlcFrom, nlcTo, fareDetail));
                                if (reversibleFlowIds.contains(tFlowId)) {
                                    fares.add(new Fare(nlcTo, nlcFrom, fareDetail));
                                }
                            }
                        }

                }
            });
        }
        LOG.info(fares.size() + " entries found");
        return fares;
    }

}
