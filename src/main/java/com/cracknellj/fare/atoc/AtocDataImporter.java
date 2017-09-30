package com.cracknellj.fare.atoc;

import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.objects.Fare;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AtocDataImporter {
    private static final Logger LOG = LogManager.getLogger(AtocDataImporter.class);

    public static void main(String[] args) throws Exception {
        AtocDataReader atocDataReader = new AtocDataReader();
        Map<String, List<Fare>> faresByStationId = atocDataReader.getFaresByStationId();
        faresByStationId.keySet().removeIf(s -> !s.equals("910GHATFILD"));
        FareDAO fareDAO = new FareDAO();
        for (String stationId : faresByStationId.keySet()) {
            List<Fare> faresList = faresByStationId.get(stationId);
            LOG.info(stationId + ", inserting fares, count: " + faresList.size());
            fareDAO.bulkInsertFares(faresList);
        }
    }
}

