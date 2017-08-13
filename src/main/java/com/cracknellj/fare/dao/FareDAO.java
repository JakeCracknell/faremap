package com.cracknellj.fare.dao;

import com.cracknellj.fare.atoc.AtocFare;
import com.cracknellj.fare.ws.objects.FareSet;
import com.cracknellj.fare.ws.objects.FareSetBuilder;
import com.cracknellj.fare.ws.objects.Station;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class FareDAO extends AbstractDAO {
    private static final Logger LOG = LogManager.getLogger(FareDAO.class);

    public FareSet getFaresFrom(String fromId) throws SQLException {
        try {
            FareSetBuilder fareSetBuilder = new FareSetBuilder(fromId);
            connectToDatabase();
            ps = cn.prepareStatement(
                    "SELECT to_id, price, mode, off_peak_only, route_description, is_default_route, accounting " +
                            "FROM fare WHERE from_id = ?");
            ps.setString(1, fromId);
            rs = ps.executeQuery();
            while (rs.next()) {
                fareSetBuilder.addFare(
                        rs.getString("to_id"),
                        rs.getBigDecimal("price"),
                        rs.getBoolean("off_peak_only"),
                        rs.getString("route_description"),
                        rs.getBoolean("is_default_route"),
                        rs.getString("accounting")
                );
            }
            return fareSetBuilder.create();
        } finally {
            closeConnection();
        }
    }

    public void bulkInsertAtocFares(List<AtocFare> faresList, Map<String, Station> crsToStation, Map<String, String> nlsToCRSMap) throws SQLException {
        try {
            connectToDatabase();
            ps = cn.prepareStatement("INSERT INTO fare (from_id, to_id, price, mode, off_peak_only, " +
                    "route_description, is_default_route, accounting) (?, ?, ?, ?, ?, ?, ?, ?)");
            int i = 0;
            for (AtocFare fare : faresList) {
                System.out.println(fare);
                String fromId = crsToStation.get(nlsToCRSMap.get(fare.nlsFrom)).crs;
                String toId = crsToStation.get(nlsToCRSMap.get(fare.nlsFrom)).crs;

//                ps.setString(1, fromId);
//                ps.setString(2, toId);
//                ps.setBigDecimal(3, fare.fareDetail.price);
//                ps.setString(4, "national-rail");
//                ps.setBoolean(5, fare.fareDetail.offPeakOnly);
//                ps.setString(6, fare.fareDetail.routeDescription);
//                ps.setBoolean(7, fare.fareDetail.isDefaultRoute);
//                ps.setString(8, fare.fareDetail.accounting);
//                ps.addBatch();
                i++;
                if (i % 1000 == 0 || i == faresList.size()) {
                    //ps.executeBatch();
                    LOG.info("Inserting prices in batches, completed so far: " + i);
                }
            }
            LOG.info("Batch inserted all " + faresList.size() +  " prices");
        } finally {
            closeConnection();
        }
    }
}
