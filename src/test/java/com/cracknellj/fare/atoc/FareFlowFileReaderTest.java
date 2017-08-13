package com.cracknellj.fare.atoc;

import com.cracknellj.fare.ws.objects.FareDetail;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FareFlowFileReaderTest {
    @Test
    public void getFareDetailList() throws Exception {
        List<AtocFare> fareDetails = new FareFlowFileReader().getFaresList();
        List<AtocFare> hatWmgFares = fareDetails.stream().filter(f -> f.nlsFrom.equals("6070") && f.nlsTo.equals("6073")).collect(Collectors.toList());
        assertFalse(hatWmgFares.isEmpty());
        assertEquals(BigDecimal.valueOf(280, 2), hatWmgFares.get(0).fareDetail.price);
        System.out.println(hatWmgFares);
    }

}