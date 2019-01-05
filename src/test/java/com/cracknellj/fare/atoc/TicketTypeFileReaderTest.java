package com.cracknellj.fare.atoc;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class TicketTypeFileReaderTest {

    @Test
    public void testGetTicketCodes() throws Exception {
        Map<String, AtocTicketCode> ticketCodes = new TicketTypeFileReader().getTicketCodes();
        assertNull(ticketCodes.get("SVH"));
    }
}