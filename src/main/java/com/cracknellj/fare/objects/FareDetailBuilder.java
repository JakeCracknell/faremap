package com.cracknellj.fare.objects;

import com.cracknellj.fare.routefinding.FareDetailAndWaypoint;

import java.util.List;

public class FareDetailBuilder {
    private List<FareDetailAndWaypoint> splitTicketHops;
    private int price;
    private boolean offPeakOnly;
    private String ticketName;
    private boolean isDefaultRoute;
    private boolean isTFL;
    private boolean isRailcardsValid;
    private String routeDescription;

    public FareDetailBuilder withSplitTicketHops(List<FareDetailAndWaypoint> hops) {
        this.splitTicketHops = hops;
        return this;
    }

    public FareDetailBuilder withPrice(int price) {
        this.price = price;
        return this;
    }

    public FareDetailBuilder withOffPeakOnly(boolean offPeakOnly) {
        this.offPeakOnly = offPeakOnly;
        return this;
    }

    public FareDetailBuilder withTicketName(String ticketName) {
        this.ticketName = ticketName;
        return this;
    }

    public FareDetailBuilder withIsDefaultRoute(boolean isDefaultRoute) {
        this.isDefaultRoute = isDefaultRoute;
        return this;
    }

    public FareDetailBuilder withIsTFL(boolean isTFL) {
        this.isTFL = isTFL;
        return this;
    }

    public FareDetailBuilder withIsRailcardsValid(boolean isRailcardsValid) {
        this.isRailcardsValid = isRailcardsValid;
        return this;
    }

    public FareDetailBuilder withRouteDescription(String routeDescription) {
        this.routeDescription = routeDescription;
        return this;
    }

    public FareDetail build() {
        routeDescription = routeDescription != null ? routeDescription : "";
        if (splitTicketHops != null) {
            ticketName = "Split Ticket";
            isTFL = splitTicketHops.stream().allMatch(h -> h.fareDetail.isTFL);
            price = splitTicketHops.stream().mapToInt(h -> h.fareDetail.price).sum();
            offPeakOnly = splitTicketHops.stream().anyMatch(h -> h.fareDetail.offPeakOnly);
            isDefaultRoute = false;
        }
        return new FareDetail(splitTicketHops, price, ticketName, routeDescription, isDefaultRoute, offPeakOnly, isTFL, isRailcardsValid);
    }
}
