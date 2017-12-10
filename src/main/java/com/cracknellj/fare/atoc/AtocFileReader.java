package com.cracknellj.fare.atoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public abstract class AtocFileReader {

    private static final String FILE_PATH_PREFIX = "C:\\Users\\Jake\\Documents\\Travel Data\\RJFAF499\\";

    protected Stream<String> getStreamOfLines(String fileName) throws IOException {
        return Files.lines(Paths.get(FILE_PATH_PREFIX + fileName));
    }
}
