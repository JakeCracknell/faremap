package com.cracknellj.fare.analysis;

import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.dao.StationDAO;

public class JourneyValueAnalyserMain {

    public static void main(String[] args) throws Exception {
        new JourneyValueAnalyser(new StationDAO().getStations(), new FareDAO().getCheapestTFLFares()).run();
    }
}
