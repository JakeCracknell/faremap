package com.cracknellj.fare.atoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

public abstract class AtocFileReader {

    private static final String FILE_PATH_PREFIX = "atoc";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("ddMMyyyy");
    private static final LocalDate QUERY_DATE = LocalDate.now();
    private final Path filePath;

    public AtocFileReader(String fileExtension) throws IOException {
        this.filePath = Files.list(Paths.get(FILE_PATH_PREFIX))
                .filter(f -> f.toString().endsWith(fileExtension)).findAny()
                .orElseThrow(() -> new IOException(String.format("No file found in '%s' directory with extension, '%s'",
                        FILE_PATH_PREFIX, fileExtension)));
    }

    protected Stream<String> getStreamOfLines() throws IOException {
        return Files.lines(filePath);
    }

    protected boolean isCurrentTimeBetweenDateString(String startDateInclusive, String endDateInclusive) {
        LocalDate startDate = LocalDate.parse(startDateInclusive, DATE_FORMAT);
        if (!QUERY_DATE.isBefore(startDate)) {
            LocalDate endDate = LocalDate.parse(endDateInclusive, DATE_FORMAT);
            return !QUERY_DATE.isAfter(endDate);
        }
        return false;
    }
}
