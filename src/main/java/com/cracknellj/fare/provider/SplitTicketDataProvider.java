package com.cracknellj.fare.provider;

import com.cracknellj.fare.objects.FareSet;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.*;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SplitTicketDataProvider implements FareDataProvider {
    @Override
    public Map<String, FareSet> getAllFareSets() {
        return null;
    }

    @Override
    public FareSet getFaresFrom(String fromId) {
        try {
            return deserialiseFromFile(fromId);
        } catch (IOException e) {
            return new FareSet(fromId);
        }
    }

    private FareSet deserialiseFromFile(String fromId) throws IOException {
        final File dataFile = new File("splitticket" + "\\" + fromId + ".json.gz");
        try (BufferedInputStream inputStream = new BufferedInputStream(
                new GZIPInputStream(
                        new FileInputStream(dataFile)
                ));
             final InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            final Gson gson = new Gson();
            return gson.fromJson(new JsonReader(inputStreamReader), FareSet.class);
        }
    }
}
