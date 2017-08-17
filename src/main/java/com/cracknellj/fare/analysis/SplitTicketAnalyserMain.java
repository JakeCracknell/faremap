package com.cracknellj.fare.analysis;

import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.Station;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SplitTicketAnalyserMain {

    public static void main(String[] args) throws Exception {
        new SplitTicketAnalyser(new StationDAO().getStations(), new FareDAO().getCheapestTFLFares()).run();
    }
}
