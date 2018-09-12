package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.FareDataProvider;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class PeakTimeDijkstraSplitTicketTask extends DijkstraSplitTicketTask {
    public PeakTimeDijkstraSplitTicketTask(Map<String, Station> stations, FareDataProvider fareDataProvider, String fromId) {
        super(stations, fareDataProvider, fromId);
    }

    @Override
    Optional<FareDetail> getFareDetailIfExists(String fromId, String toId) {
        return fareDataProvider.getFares(fromId, toId).stream().filter(f -> !f.offPeakOnly)
                .min(Comparator.comparingInt(f -> f.price));
    }
}
