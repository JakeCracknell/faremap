package com.cracknellj.fare.offline.tfl;

import com.cracknellj.fare.atoc.AtocFileReader;
import com.cracknellj.fare.io.StationFileReader;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.objects.StationTag;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    @Test
    public void testCompareAgainstAtoc() throws Exception {
        List<Station> stations = StationFileReader.getStations().stream().filter(s -> s.crs != null)
                .filter(s -> s.tags.contains(StationTag.TUBE) || s.tags.contains(StationTag.DLR))
                .collect(Collectors.toList());
        Map<String, String> crsToStationDescription = new CrsAtocFileReader().getCrsToStationDescription();
        stations.forEach(s -> System.out.println(s + " - " + s.crs + " - " + crsToStationDescription.get(s.crs)));
    }

    private class CrsAtocFileReader extends AtocFileReader {

        public CrsAtocFileReader() throws IOException {
            super("LOC");
        }

        public Map<String, String> getCrsToStationDescription() throws IOException {
            return getStreamOfLines().filter(line -> line.charAt(1) == 'L')
                    .collect(Collectors.toMap(line -> line.substring(56, 59), line -> line.substring(87, 103), (a, b) -> a));
        }
    }
}
