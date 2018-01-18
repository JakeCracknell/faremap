package com.cracknellj.fare.atoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public abstract class AtocFileReader {

    private static final String FILE_PATH_PREFIX = "atoc";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");

    protected Stream<String> getStreamOfLines(String fileName) throws IOException {
        return Files.lines(Paths.get(FILE_PATH_PREFIX, fileName));
    }

    protected boolean isCurrentTimeBetweenDateString(String startDateInclusive, String endDateInclusive) {
        LocalDate startDate = LocalDate.parse(startDateInclusive, DATE_FORMAT);
        if (!LocalDate.now().isBefore(startDate)) {
            LocalDate endDate = LocalDate.parse(endDateInclusive, DATE_FORMAT);
            return !LocalDate.now().isAfter(endDate);
        }
        return false;
    }
}
