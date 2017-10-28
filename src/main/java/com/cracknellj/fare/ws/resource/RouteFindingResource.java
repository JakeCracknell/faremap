package com.cracknellj.fare.ws.resource;

import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.objects.Station;
import com.cracknellj.fare.provider.CompositeSingletonFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import com.cracknellj.fare.routefinding.DijkstraRouteFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Path("routefinding")
public class RouteFindingResource extends AbstractResource {
    private static final Logger LOG = LogManager.getLogger(RouteFindingResource.class);
    private final FareDataProvider fareDataProvider;
    private final List<Station> stations;

    public RouteFindingResource() throws SQLException {
        this(CompositeSingletonFareDataProvider.getInstance(), new StationDAO().getStations());
    }

    public RouteFindingResource(FareDataProvider fareDataProvider, List<Station> stations) {
        this.fareDataProvider = fareDataProvider;
        this.stations = stations;
    }

    @GET
    @Path("/from/{fromId}/to/{toId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String findCheapestRoute(@PathParam("fromId") String fromId) throws WebApplicationException {
        try {
            LOG.info("Request to GET cheapest routes received, from " + fromId);
            DijkstraRouteFinder dijkstraRouteFinder = new DijkstraRouteFinder(stations, fareDataProvider);
            FareSet fareSet = dijkstraRouteFinder.findCheapestRoutes(fromId);
            String json = getGson().toJson(fareSet);
            LOG.info(truncate(json));
            return json;
        } catch (Exception e) {
            LOG.error("Failed to find cheapest routes", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
