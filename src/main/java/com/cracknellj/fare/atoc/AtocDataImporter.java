package com.cracknellj.fare.atoc;

import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.ws.objects.Station;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AtocDataImporter {
    public static void main(String[] args) throws Exception {
        Map<String, Station> crsToStation = new StationDAO().getStations().stream().filter(s -> s.crs != null).collect(Collectors.toMap(s -> s.crs, s -> s));
        Map<String, String> nlsToCRSMap = new LocationFileReader().getNLSToCRSMap();
        List<AtocFare> faresList = new FareFlowFileReader().getFaresList();

        new FareDAO().bulkInsertAtocFares(faresList, crsToStation, nlsToCRSMap);
    }
}

