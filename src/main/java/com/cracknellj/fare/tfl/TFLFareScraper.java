package com.cracknellj.fare.tfl;

import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareDetail;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TFLFareScraper {
    private static final Logger LOG = LogManager.getLogger(TFLFareScraper.class);
    private final Gson gson = new Gson();

    public List<Fare> lookupFare(Fare fare) {
        return lookupFare(fare.fromId, fare.toId);
    }

    public List<Fare> lookupFare(String fromId, String toId) {
        List<FareDetail> fareDetails = new ArrayList<>();
        String urlString = String.format("https://api.tfl.gov.uk/Stoppoint/%s/FareTo/%s", fromId, toId);
        try {
            URL url = new URL(urlString);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            TFLResponseFareSection[] tflResponseFareSections =
                    gson.fromJson(new InputStreamReader(request.getInputStream()), TFLResponseFareSection[].class);
            for (TFLResponseFareSection section : tflResponseFareSections) {
                for (TFLResponseRow row : section.rows) {
                    for (TFLResponseTicket ticket : row.ticketsAvailable) {
                        fareDetails.add(new FareDetail(ticket.cost, "Off Peak".equals(ticket.ticketTime.type),
                                row.routeDescription, section.index == 1, ticket.ticketType.type, true));
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("Failed to get " + urlString, e);
        }
        LOG.info(String.format("Retrieved %d fares from %s", fareDetails.size(), urlString));
        return fareDetails.stream().map(fd -> new Fare(fromId, toId, fd)).collect(Collectors.toList());
    }

    private class TFLResponseFareSection {
        public Integer index;
        public List<TFLResponseRow> rows;
    }

    private class TFLResponseRow {
        public String routeDescription;
        public List<TFLResponseTicket> ticketsAvailable;
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
