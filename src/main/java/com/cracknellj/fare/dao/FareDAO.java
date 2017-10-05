package com.cracknellj.fare.dao;

import com.cracknellj.fare.objects.Fare;
import com.cracknellj.fare.objects.FareDetail;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.FareSetBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FareDAO extends AbstractDAO {
    private static final Logger LOG = LogManager.getLogger(FareDAO.class);

    public Map<String, FareSet> getAllFares() throws SQLException {
        try {
            Map<String, FareSet> fareSets = new HashMap<>();
            connectToDatabase();
            ps = cn.prepareStatement(
                    "SELECT from_id, to_id, price, off_peak_only, route_description, is_default_route, accounting, " +
                            "is_tfl FROM fare");
            rs = ps.executeQuery();
            rs.setFetchSize(100000);
            while (rs.next()) {
                String fromId = rs.getString("from_id");
                fareSets.computeIfAbsent(fromId, x -> new FareSet(fromId))
                        .add(rs.getString("to_id"),
                                new FareDetail(
                                        rs.getBigDecimal("price"),
                                        rs.getBoolean("off_peak_only"),
                                        rs.getString("route_description"),
                                        rs.getBoolean("is_default_route"),
                                        rs.getString("accounting"),
                                        rs.getBoolean("is_tfl")
                                )
                        );
            }
            return fareSets;
        } finally {
            closeConnection();
        }
    }

    public FareSet getFaresFrom(String fromId) throws SQLException {
        try {
            FareSetBuilder fareSetBuilder = new FareSetBuilder(fromId);
            connectToDatabase();
            ps = cn.prepareStatement(
                    "SELECT to_id, price, off_peak_only, route_description, is_default_route, accounting, is_tfl " +
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
                        rs.getString("accounting"),
                        rs.getBoolean("is_tfl")
                );
            }
            return fareSetBuilder.create();
        } finally {
            closeConnection();
        }
    }

    public void bulkInsertFares(List<Fare> faresList) throws SQLException {
        try {
            connectToDatabase();
            ps = cn.prepareStatement("INSERT INTO fare (from_id, to_id, price, off_peak_only, " +
                    "route_description, is_default_route, accounting, is_tfl) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
            int i = 0;
            LOG.info("About to insert " + faresList.size() + " fares");
            for (Fare fare : faresList) {
                ps.setString(1, fare.fromId);
                ps.setString(2, fare.toId);
                ps.setBigDecimal(3, fare.fareDetail.price);
                ps.setBoolean(4, fare.fareDetail.offPeakOnly);
                ps.setString(5, fare.fareDetail.routeDescription);
                ps.setBoolean(6, fare.fareDetail.isDefaultRoute);
                ps.setString(7, fare.fareDetail.accounting);
                ps.setBoolean(8, fare.fareDetail.isTFL);
                ps.addBatch();
                i++;
                if (i % 1000 == 0 || i == faresList.size()) {
                    ps.executeBatch();
                    LOG.info("Inserting fares in batches, completed so far: " + i);
                }
            }
            LOG.info("Batch inserted all " + faresList.size() + " fares");
        } finally {
            closeConnection();
        }
    }

    public Map<String, Map<String, BigDecimal>> getCheapestTFLFares() throws SQLException {
        try {
            Map<String, Map<String, BigDecimal>> fares = new HashMap<>();
            connectToDatabase();
            ps = cn.prepareStatement(
                    "SELECT from_id, to_id, price " +
                            "FROM fare WHERE accounting = 'Pay as you go' and off_peak_only = false");
            rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, BigDecimal> fromMap = fares.computeIfAbsent(rs.getString("from_id"), a -> new HashMap<>());
                String toId = rs.getString("to_id");
                BigDecimal newPrice = rs.getBigDecimal("price");
                BigDecimal existingPrice = fromMap.get(toId);
                if (existingPrice == null || existingPrice.compareTo(newPrice) > 0) {
                    fromMap.put(toId, newPrice);
                }
            }
            return fares;
        } finally {
            closeConnection();
        }
    }
}
