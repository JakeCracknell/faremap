package com.cracknellj.fare.ws.resource;

import com.cracknellj.fare.atoc.AtocDataService;
import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.objects.FareSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("fare")
public class FareResource extends AbstractResource {
    private static final Logger LOG = LogManager.getLogger(FareResource.class);
    private final FareDAO fareDAO;
    private final AtocDataService atocDataService;

    public FareResource() {
        this(new FareDAO(), null);
    }

    public FareResource(FareDAO fareDAO, AtocDataService atocDataService) {
        this.fareDAO = fareDAO;
        this.atocDataService = atocDataService;
    }

    @GET
    @Path("/from/{fromId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFaresFrom(@PathParam("fromId") String fromId) throws WebApplicationException {
        try {
            LOG.info("Request to GET fares received, from " + fromId);
            FareSet fareSet = fareDAO.getFaresFrom(fromId);
            String json = getGson().toJson(fareSet);
            LOG.info(truncate(json));
            return json;
        } catch (Exception e) {
            LOG.error(e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
