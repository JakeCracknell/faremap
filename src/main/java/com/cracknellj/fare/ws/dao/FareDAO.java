package com.cracknellj.fare.ws.dao;

import com.cracknellj.fare.ws.objects.FareSet;
import com.cracknellj.fare.ws.objects.FareSetBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

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
}
