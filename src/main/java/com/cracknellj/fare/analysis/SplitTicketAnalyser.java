package com.cracknellj.fare.analysis;

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

public class SplitTicketAnalyser {
    private static final BigDecimal NO_PRICE_PLACEHOLDER = BigDecimal.valueOf(99999);
    private static final Logger LOG = LogManager.getLogger(SplitTicketAnalyser.class);
    private final List<Station> stations;
    private final Map<String, Map<String, BigDecimal>> fares;

    public SplitTicketAnalyser(List<Station> stations, Map<String, Map<String, BigDecimal>> fares) {
        this.stations = stations;
        this.fares = fares;
    }

    public void run() throws IOException {
        FileWriter fw = new FileWriter("G:\\split-ticket-analysis.csv");
        for (Station stationFrom : stations) {
            for (Station stationTo : stations) {
                BigDecimal defaultPrice = getPrice(stationFrom, stationTo);
                if (defaultPrice.equals(NO_PRICE_PLACEHOLDER) || defaultPrice.doubleValue() == 0) continue;
                Station cheapestStationVia = stationFrom;
                BigDecimal cheapestSplitTicketPrice = defaultPrice;
                for (Station stationVia : stations) {
                    BigDecimal splitTicketPrice = getSplitTicketPrice(stationFrom, stationTo, stationVia);
                    if (cheapestSplitTicketPrice.compareTo(splitTicketPrice) > 0) {
                        cheapestStationVia = stationVia;
                        cheapestSplitTicketPrice = splitTicketPrice;
                    }
                }
                String csvLine = Joiner.on(",").join(Arrays.asList(
                        stationFrom.stationName, stationTo.stationName, cheapestStationVia.stationName,
                        defaultPrice.toString(), cheapestSplitTicketPrice.toString(),
                        defaultPrice.add(cheapestSplitTicketPrice.negate()),
                        cheapestSplitTicketPrice.divide(defaultPrice, 2, BigDecimal.ROUND_HALF_UP)
                ));
                LOG.info(csvLine);
                fw.write(csvLine);
                fw.write("\n");
            }
        }
        fw.close();
    }

    private BigDecimal getSplitTicketPrice(Station stationFrom, Station stationTo, Station stationVia) {
        return getPrice(stationFrom, stationVia).add(getPrice(stationVia, stationTo));
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
