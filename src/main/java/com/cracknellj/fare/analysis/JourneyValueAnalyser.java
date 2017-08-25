package com.cracknellj.fare.analysis;

import com.cracknellj.fare.Haversine;
import com.cracknellj.fare.objects.Station;
import jersey.repackaged.com.google.common.base.Joiner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JourneyValueAnalyser {
    private static final BigDecimal NO_PRICE_PLACEHOLDER = BigDecimal.valueOf(99999);
    private static final Logger LOG = LogManager.getLogger(JourneyValueAnalyser.class);
    private final List<Station> stations;
    private final Map<String, Map<String, BigDecimal>> fares;

    public JourneyValueAnalyser(List<Station> stations, Map<String, Map<String, BigDecimal>> fares) {
        this.stations = stations;
        this.fares = fares;
    }

    public void run() throws IOException {
        FileWriter fw = new FileWriter("G:\\Data Analysis\\journey-value-analysis.csv");
        for (Station stationFrom : stations) {
            for (Station stationTo : stations) {
                BigDecimal defaultPrice = getPrice(stationFrom, stationTo);
                if (defaultPrice.equals(NO_PRICE_PLACEHOLDER) || defaultPrice.doubleValue() == 0) continue;

                double distance = Haversine.distance(stationFrom.latitude, stationFrom.longitude,
                        stationTo.latitude, stationTo.longitude);

                String csvLine = Joiner.on(",").join(Arrays.asList(
                        stationFrom.stationName, stationTo.stationName,
                        distance,
                        defaultPrice.toString(),
                        defaultPrice.doubleValue() / distance
                ));
                LOG.info(csvLine);
                fw.write(csvLine);
                fw.write("\n");
            }
        }
        fw.close();
    }

    private BigDecimal getPrice(Station stationFrom, Station stationTo) {
        Map<String, BigDecimal> fromMap = fares.get(stationFrom.stationId);
        if (fromMap != null) {
            BigDecimal price = fromMap.get(stationTo.stationId);
            if (price != null) {
                return price;
            }
        }
        return NO_PRICE_PLACEHOLDER;
    }
}
