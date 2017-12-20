package com.cracknellj.fare.atoc;

import com.cracknellj.fare.objects.AtocTicketCode;
import jersey.repackaged.com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class TicketTypeFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(TicketTypeFileReader.class);

    public static final String FILE_NAME = "RJFAF499.TTY";

    public Map<String, AtocTicketCode> getTicketCodes() throws IOException {
        List<AtocTicketCode> ticketCodes = new ArrayList<>();
        try (Stream<String> lineStream = getStreamOfLines(FILE_NAME)) {
            lineStream.filter(l -> !l.startsWith("/")).forEach(line -> {
                char ticketJourneyType = line.charAt(44); // We want single
                char ticketClass = line.charAt(45); // We want standard
                if (ticketJourneyType == 'S' && ticketClass == 'S') {
                    String ticketCode = line.substring(1, 4);
                    String description = line.substring(28, 28 + 15).trim();
                    if (description.startsWith("ANYTIME")) {
                        ticketCodes.add(new AtocTicketCode(ticketCode, description));
                    }
                }
            });
        }
        LOG.info(ticketCodes.size() + " entries found");
        return Maps.uniqueIndex(ticketCodes, t -> t.ticketCode);
    }

}
