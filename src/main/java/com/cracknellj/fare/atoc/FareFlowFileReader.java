package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareDetailBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

public class FareFlowFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(FareFlowFileReader.class);
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
                        String endDate = line.substring(20, 28);
                        String startDate = line.substring(28, 36);
                        if (isCurrentTimeBetweenDateString(startDate, endDate)) {
                            String flowId = line.substring(42, 49);
                            String fromNlc = line.substring(2, 6);
                            String toNlc = line.substring(6, 10);
                            String routeCode = line.substring(10, 15);
                            boolean reversible = line.charAt(19) == 'R';
                            flowMap.put(flowId, new AtocFlowRecord(fromNlc, toNlc, routeCode, reversible));
                        }
                    case 'T':
                        String restriction = line.substring(20, 22);
                        String ticketCodeString = line.substring(9, 12);
                        if (ticketCodes.containsKey(ticketCodeString)) {
                            AtocTicketCode ticketCode = ticketCodes.get(ticketCodeString);
                            String tFlowId = line.substring(2, 9);
                            String farePence = line.substring(12, 20);
                            AtocFlowRecord atocFlowRecord = flowMap.get(tFlowId);
                            if (atocFlowRecord != null) {
                                //e.g. SDS OFF-PEAK S VW
                                String ticketName = ticketCode.ticketCode + " " + ticketCode.description.trim() + " " + restriction;
                                FareDetail fare = new FareDetailBuilder()
                                        .withPrice(Integer.parseInt(farePence))
                                        .withOffPeakOnly(ticketCode.isOffPeak())
                                        .withTicketName(ticketName)
                                        .withIsDefaultRoute(true)
                                        .withIsTFL(false)
                                        .withIsRailcardsValid(true)
                                        .build();
                                fares.add(new AtocFare(atocFlowRecord.fromNlc, atocFlowRecord.toNlc,
                                        atocFlowRecord.routeCode, fare));
                                if (atocFlowRecord.reversible) {
                                    fares.add(new AtocFare(atocFlowRecord.toNlc, atocFlowRecord.fromNlc,
                                            atocFlowRecord.routeCode, fare));
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
