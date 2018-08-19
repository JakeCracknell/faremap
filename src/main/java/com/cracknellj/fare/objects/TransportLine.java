package com.cracknellj.fare.objects;

import java.util.List;

public class TransportLine {
    public final String lineId; // e.g. arriva-trains-wales
    public final String lineName; // e.g. Arriva Trains Wales
    public final List<TransportLineBranch> branches;

    public TransportLine(String lineId, String lineName, List<TransportLineBranch> branches) {
        this.lineId = lineId;
        this.lineName = lineName;
        this.branches = branches;
    }

    public static class TransportLineBranch {
        public final String branchName;
        public final Direction direction;
        public final List<String> stationIds;

        public TransportLineBranch(String branchName, Direction direction, List<String> stationIds) {
            this.branchName = branchName;
            this.direction = direction;
            this.stationIds = stationIds;
        }
    }

    public enum Direction {
        INBOUND, OUTBOUND
    }
}
