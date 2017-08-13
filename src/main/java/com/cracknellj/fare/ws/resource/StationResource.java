package com.cracknellj.fare.ws.resource;

import com.cracknellj.fare.dao.StationDAO;
import com.cracknellj.fare.ws.objects.Station;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("station")
public class StationResource extends AbstractResource {
    private static final Logger LOG = LogManager.getLogger(StationResource.class);
    private final StationDAO stationDAO;

    public StationResource() {
        stationDAO = new StationDAO();
    }

    public StationResource(StationDAO stationDAO) {
        this.stationDAO = stationDAO;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStations() throws WebApplicationException {
        try {
            LOG.info("Request to GET stations received");
            List<Station> stations = stationDAO.getStations();
            String json = getGson().toJson(stations);
            LOG.info(truncate(json));
            return json;
        } catch (Exception e) {
            LOG.error(e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
