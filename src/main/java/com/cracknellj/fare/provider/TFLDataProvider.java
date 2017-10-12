package com.cracknellj.fare.provider;

import com.cracknellj.fare.atoc.AtocDataReader;
import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.objects.FareSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TFLDataProvider implements FareDataProvider {
    private static final Logger LOG = LogManager.getLogger(AtocDataReader.class);

    private Map<String, FareSet> fareSets = new HashMap<>();

    public TFLDataProvider() {
        try {
            LOG.info("Loading fares from DAO");
            fareSets = new FareDAO().getAllFares();
            LOG.info("Successfully loaded all fares from DAO");
        } catch (SQLException e) {
            LOG.error("Failed to read TFL Fares", e);
        }
    }

    @Override
    public Map<String, FareSet> getAllFareSets() {
        return fareSets;
    }
}
