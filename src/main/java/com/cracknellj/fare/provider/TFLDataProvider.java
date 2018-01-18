package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareSet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class TFLDataProvider implements FareDataProvider {
    private static final Logger LOG = LogManager.getLogger(TFLDataProvider.class);

    @Override
    public Map<String, FareSet> getAllFareSets() {
        Map<String, FareSet> fareSets = new HashMap<>();
        try {
            LOG.info("Loading TFL data from file");
            fareSets = deserialiseFromFile();
            LOG.info("Loading TFL data from file. Complete");
        } catch (Exception e) {
            LOG.error("Failed to read TFL Fares", e);
        }
        return fareSets;
    }

    private Map<String, FareSet> deserialiseFromFile() throws IOException {
        final File dataFile = new File("tfl.json.gz");
        try (BufferedInputStream inputStream = new BufferedInputStream(
                new GZIPInputStream(
                        new FileInputStream(dataFile)
                ));
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            final Gson gson = new Gson();
            return gson.fromJson(new JsonReader(inputStreamReader), new TypeToken<Map<String, FareSet>>(){}.getType());
        }
    }

}
