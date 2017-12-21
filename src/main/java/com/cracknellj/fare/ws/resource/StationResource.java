package com.cracknellj.fare.ws.resource;

import com.cracknellj.fare.io.StationFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("station")
public class StationResource extends AbstractResource {
    private static final Logger LOG = LogManager.getLogger(StationResource.class);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getStations() throws WebApplicationException {
        try {
            LOG.info("Request to GET stations received");
            String json = getGson().toJson(StationFileReader.getStations());
            LOG.info(truncate(json));
            return json;
        } catch (Exception e) {
            LOG.error("Failed to get stations", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
