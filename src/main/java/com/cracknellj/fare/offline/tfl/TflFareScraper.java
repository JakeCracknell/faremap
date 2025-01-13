package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareDetailBuilder;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TflFareScraper {
    private static final Logger LOG = LogManager.getLogger(TflFareScraper.class);
    private static final int MAX_TRIES = 10;
    private static final BigDecimal PRICE_MULTIPLICAND = BigDecimal.valueOf(100);
    private final Gson gson = new Gson();

    public List<Fare> lookupFare(String fromId, String toId) {
        String urlString = String.format("https://api.tfl.gov.uk/Stoppoint/%s/FareTo/%s/" +
                "?app_id=8268063a&app_key=14f7f5ff5d64df2e88701cef2049c804", fromId, toId);
        List<FareDetail> fareDetails = lookupFareInternalMultiTry(urlString);
        LOG.info(String.format("Retrieved %d fares from %s", fareDetails.size(), urlString));
        return fareDetails.stream().map(fd -> new Fare(fromId, toId, fd)).collect(Collectors.toList());
    }

    private List<FareDetail> lookupFareInternalMultiTry(String urlString)  {
        for (int tryNumber = 1; tryNumber <= MAX_TRIES; tryNumber++) {
            try {
                if (tryNumber > 1) {
                    Thread.sleep(tryNumber * 1000);
                }
                return lookupFareInternalOneTry(urlString);
            } catch (Exception e) {
                LOG.error(String.format("Failed to get %s. Try [%d/%d] (%s)", urlString, tryNumber, MAX_TRIES, e));
            }
        }
        return Collections.emptyList();
    }

    private List<FareDetail> lookupFareInternalOneTry(String urlString) throws IOException {
        List<FareDetail> fareDetails = new ArrayList<>();
        URL url = new URL(urlString);
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();
        TFLResponseFareSection[] tflResponseFareSections =
                gson.fromJson(new InputStreamReader(request.getInputStream()), TFLResponseFareSection[].class);
        for (TFLResponseFareSection section : tflResponseFareSections) {
            for (TFLResponseRow row : section.rows) {
                for (TFLResponseTicket ticket : row.ticketsAvailable) {
                    if (!ticket.ticketType.type.equals("CashSingle")) {
                        String ticketName = "TFL " + (row.contactlessPAYGOnlyFare ? "" : "Oyster / ") + "Contactless";
                        boolean isOffPeak = "Off Peak".equals(ticket.ticketTime.type);
                        FareDetail fareDetail = new FareDetailBuilder()
                                .withPrice(ticket.cost.multiply(PRICE_MULTIPLICAND).intValue())
                                .withOffPeakOnly(isOffPeak)
                                .withTicketName(ticketName)
                                .withRouteDescription(row.routeDescription)
                                .withIsDefaultRoute(section.index == 1)
                                .withIsTFL(true)
                                .withIsRailcardsValid(!row.contactlessPAYGOnlyFare && isOffPeak)
                                .build();
                        fareDetails.add(fareDetail);
                    }
                }
            }
        }
        return fareDetails;
    }

    private class TFLResponseFareSection {
        public Integer index;
        public List<TFLResponseRow> rows;
    }

    private class TFLResponseRow {
        public String routeDescription;
        public List<TFLResponseTicket> ticketsAvailable;
        public boolean contactlessPAYGOnlyFare;
    }

    private class TFLResponseTicket {
        public BigDecimal cost;
        public TFLResponseTicketType ticketType;
        public TFLResponseTicketTime ticketTime;
    }

    private class TFLResponseTicketType {
        public String type;
    }

    private class TFLResponseTicketTime {
        public String type;
    }
}
