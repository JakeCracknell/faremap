package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.objects.TransportLine;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.IOUtils;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class TflLinesScraper {
    private static final Logger LOG = LogManager.getLogger(TflFareScraper.class);
    public static final int MAX_TRIES = 10;
    private final Gson gson = new Gson();

    public Set<String> getLineIds() {
        String json = getJsonFromUrl("https://api.tfl.gov.uk/Line/Mode/dlr%2Ctube%2Ctflrail%2Coverground%2Cnational-rail")
                .orElseThrow(() -> new RuntimeException("Failed to load line IDs, so cannot proceed"));
        ObjectWithId[] objectsWithIds = gson.fromJson(json, ObjectWithId[].class);
        return Arrays.stream(objectsWithIds).map(o -> o.id).collect(Collectors.toSet());
    }

    private Optional<String> getJsonFromUrl(String urlString) {
        LOG.info(urlString);
        for (int tryNumber = 1; tryNumber <= MAX_TRIES; tryNumber++) {
            try {
                if (tryNumber > 1) {
                    Thread.sleep(tryNumber * 1000);
                }
                URL url = new URL(urlString);
                HttpURLConnection request = (HttpURLConnection) url.openConnection();
                request.connect();
                return Optional.ofNullable(IOUtils.toString(new InputStreamReader(request.getInputStream())));
            } catch (Exception e) {
                LOG.error(String.format("Failed to get %s. Try [%d/%d] (%s)", urlString, tryNumber, MAX_TRIES, e));
            }
        }
        return Optional.empty();
    }

    public Optional<TransportLine> getTransportLine(String lineId) {
        try {
            List<TflRouteSequenceResponse> responses = Arrays.stream(TransportLine.Direction.values())
                    .map(dir -> getJsonFromUrl("https://api.tfl.gov.uk/line/" + lineId +
                            "/route/sequence/" + dir + "?formatter=json").orElseThrow(RuntimeException::new))
                    .map(json -> gson.fromJson(json, TflRouteSequenceResponse.class)).collect(Collectors.toList());

            TransportLine transportLine = new TransportLine(lineId, responses.get(0).lineName,
                    responses.stream().flatMap(r -> r.orderedLineRoutes.stream().map(olr ->
                            new TransportLine.TransportLineBranch(olr.name, r.direction, olr.naptanIds))).collect(Collectors.toList())
            );
            return Optional.of(transportLine);
        } catch (Exception e) {
            LOG.error(String.format("Failed to parse %s", lineId), e);
            return Optional.empty();
        }
    }

    private class ObjectWithId {
        public String id;
    }

    private class TflRouteSequenceResponse {
        public String lineName;
        public TransportLine.Direction direction;
        public List<TflOrderedRoute> orderedLineRoutes;
    }

    private class TflOrderedRoute {
        public String name;
        public List<String> naptanIds;
    }
}
