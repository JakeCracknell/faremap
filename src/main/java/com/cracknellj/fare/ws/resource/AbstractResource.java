package com.cracknellj.fare.ws.resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class AbstractResource {
    static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
    public static final int TRUNCATED_LENGTH = 100;

    Gson getGson() {
        return new GsonBuilder().setDateFormat(TIMESTAMP_FORMAT).create();
    }

    String truncate(String string) {
        if (string.length() > TRUNCATED_LENGTH) {
            return string.substring(0, TRUNCATED_LENGTH).replace("\n", "\\n") + "... (length " + string.length() + ")";
        }
        return string;
    }

    boolean isValidId(String id) {
        return id.matches("\\d+");
    }
}
