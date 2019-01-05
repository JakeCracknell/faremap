package com.cracknellj.fare.atoc;

public class AtocTicketCode {
    public final String ticketCode;
    public final String description;
    public final boolean offPeak;

    public AtocTicketCode(String ticketCode, String description, boolean offPeak) {
        this.ticketCode = ticketCode;
        this.description = description;
        this.offPeak = offPeak;
    }

    @Override
    public String toString() {
        return "AtocTicketCode{" +
                "ticketCode='" + ticketCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean isOffPeak() {
        return offPeak;
    }
}
