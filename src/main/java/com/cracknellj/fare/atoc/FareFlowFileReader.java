package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.AtocTicketCode;
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
    private static final String FILE_EXTENSION = "FFL";

    public FareFlowFileReader() throws IOException {
        super(FILE_EXTENSION);
    }

    //RF6133Q63000028000GS3112299921052017FCC00Y0277177
    public List<AtocFare> getFaresList() throws IOException {
        Map<String, AtocTicketCode> ticketCodes = new TicketTypeFileReader().getTicketCodes();
        List<AtocFare> fares = new ArrayList<>();
        Map<String, AtocFlowRecord> flowMap = new HashMap<>();
        try (Stream<String> lineStream = getStreamOfLines()) {
            lineStream.forEach(line -> {
                switch (line.charAt(1)) {
                    case 'F':
                        boolean publishable = line.charAt(41) == 'Y';
                        if (publishable) {
                            String flowId = line.substring(42, 49);
                            String fromNlc = line.substring(2, 6);
                            String toNlc = line.substring(6, 10);
                            String routeCode = line.substring(10, 15);
                            boolean reversible = line.charAt(19) == 'R';
                            flowMap.put(flowId, new AtocFlowRecord(fromNlc, toNlc, routeCode, reversible));
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
                                AtocFlowRecord atocFlowRecord = flowMap.get(tFlowId);
                                if (atocFlowRecord != null) {
                                    FareDetail fareDetail = new FareDetail(farePrice, ticketCode.isOffPeak(), ticketCode.description, ticketCode.isDefaultFare(), "NR", false);
                                    fares.add(new AtocFare(atocFlowRecord.fromNlc, atocFlowRecord.toNlc, atocFlowRecord.reversible, atocFlowRecord.routeCode, fareDetail));
                                }
                            }
                        }

                }
            });
        }
        LOG.info(fares.size() + " entries found");
        return fares;
    }

    private class AtocFlowRecord {
        private final String fromNlc;
        private final String toNlc;
        private final String routeCode;
        private final boolean reversible;

        private AtocFlowRecord(String fromNlc, String toNlc, String routeCode, boolean reversible) {
            this.fromNlc = fromNlc;
            this.toNlc = toNlc;
            this.routeCode = routeCode;
            this.reversible = reversible;
        }
    }

}
