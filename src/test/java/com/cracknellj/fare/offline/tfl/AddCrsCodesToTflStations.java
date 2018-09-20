package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.io.StationFileReader;
import org.junit.Test;

import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

//Mostly correct: https://techforum.tfl.gov.uk/t/tube-stations-with-crs-and-nlc-codes/612/3
public class AddCrsCodesToTflStations {

    @Test
    public void showMissingCrsCodes() throws Exception {
        StationFileReader.getStations().stream().filter(s -> s.crs == null || s.crs.isEmpty()).forEach(System.out::println);
    }

    @Test
    public void testNoDuplicateCrs() throws Exception {
        StationFileReader.getStations().stream().filter(s -> s.crs != null).collect(groupingBy(s -> s.crs, Collectors.toList()))
                .entrySet().stream().filter(e -> e.getValue().size() > 1).forEach(System.out::println);
    }
}
