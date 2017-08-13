package com.cracknellj.fare.objects;

public class AtocTicketCode {
    public final String ticketCode;
    public final String description;
    public final boolean offPeak;

    public AtocTicketCode(String ticketCode, String description) {
        this.ticketCode = ticketCode;
        this.description = description;
        this.offPeak = !description.startsWith("ANYTIME");
    }

    @Override
    public String toString() {
        return "AtocTicketCode{" +
                "ticketCode='" + ticketCode + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    public boolean isDefaultFare() {
        return !offPeak;
    }

    public boolean isOffPeak() {
        return offPeak;
    }
}
