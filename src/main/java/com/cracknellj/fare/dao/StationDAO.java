package com.cracknellj.fare.dao;

import com.cracknellj.fare.objects.Station;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StationDAO extends AbstractDAO {

    public List<Station> getStations() throws SQLException {
        try {
            List<Station> stations = new ArrayList<>();
            connectToDatabase();
            ps = cn.prepareStatement(
                    "SELECT station_id, station_name, modes_list, oyster_accepted, latitude, longitude, crs " +
                            "FROM station ");
            rs = ps.executeQuery();
            while (rs.next()) {
                List<String> modes = Stream.of(rs.getString("modes_list").split(",")).map(String::trim).collect(Collectors.toList());
                Station station = new Station(
                        rs.getString("station_id"),
                        rs.getString("station_name"),
                        modes,
                        rs.getBoolean("oyster_accepted"),
                        rs.getFloat("latitude"),
                        rs.getFloat("longitude"),
                        rs.getString("crs"));
                stations.add(station);
            }
            return stations;
        } finally {
            closeConnection();
        }
    }
}
