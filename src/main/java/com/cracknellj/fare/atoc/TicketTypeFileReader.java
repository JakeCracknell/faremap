package com.cracknellj.fare.atoc;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TicketTypeFileReader extends AtocFileReader {
    private static final Logger LOG = LogManager.getLogger(TicketTypeFileReader.class);

    private static final String FILE_EXTENSION = "TTY";
    private static final Pattern OFF_PEAK_PATTERN = Pattern.compile("((SUP(ER)?) )?OFF.?P.*K.* (DAY|S)");
    private static final Set<String> TICKET_CODE_BLACKLIST = Sets.newHashSet("SVH", "SSH", "SSU", "SS2");

    public TicketTypeFileReader() throws IOException {
        super(FILE_EXTENSION);
    }

    public Map<String, AtocTicketCode> getTicketCodes() throws IOException {
        List<AtocTicketCode> ticketCodes = new ArrayList<>();
        try (Stream<String> lineStream = getStreamOfLines()) {
            lineStream.filter(l -> !l.startsWith("/")).forEach(line -> {
                char ticketJourneyType = line.charAt(44); // We want single
                char ticketClass = line.charAt(45); // We want standard
                if (isCurrentTimeBetweenDateString(line.substring(20, 28), line.substring(4, 12))) {
                    if (ticketJourneyType == 'S' && ticketClass == 'S') {
                        String ticketCode = line.substring(1, 4);
                        if (!TICKET_CODE_BLACKLIST.contains(ticketCode)) {
                            String description = line.substring(28, 28 + 15).trim();
                            if (description.startsWith("ANYTIME")) {
                                ticketCodes.add(new AtocTicketCode(ticketCode, description, false));
                            } else if (OFF_PEAK_PATTERN.matcher(description).matches()) {
                                ticketCodes.add(new AtocTicketCode(ticketCode, description, true));
                            } // otherwise it is advance, carnet, smart, unusual, etc...
                        }
                    }
                }
            });
        }
        LOG.info(ticketCodes.size() + " entries found");
        return Maps.uniqueIndex(ticketCodes, t -> t.ticketCode);
    }

}
