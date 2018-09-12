package com.cracknellj.fare.routefinding;

import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.FareDataProvider;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

public class PeakTimeDijkstraSplitTicketTask extends DijkstraSplitTicketTask {
    public PeakTimeDijkstraSplitTicketTask(Collection<Station> stations, FareDataProvider fareDataProvider) {
        super(stations, fareDataProvider);
    }

    @Override
    Optional<FareDetail> getFareDetailIfExists(String fromId, String toId) {
        return fareDataProvider.getFares(fromId, toId).stream().filter(f -> !f.offPeakOnly)
                .min(Comparator.comparingInt(f -> f.price));
    }
}
