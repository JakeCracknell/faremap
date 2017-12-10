package com.cracknellj.fare.ws.resource;

import com.cracknellj.fare.provider.AtocDataProvider;
import com.cracknellj.fare.dao.FareDAO;
import com.cracknellj.fare.objects.FareSet;
import com.cracknellj.fare.provider.CompositeSingletonFareDataProvider;
import com.cracknellj.fare.provider.FareDataProvider;
import com.cracknellj.fare.provider.SplitTicketDataProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("fare")
public class FareResource extends AbstractResource {
    private static final Logger LOG = LogManager.getLogger(FareResource.class);
    private final FareDataProvider fareDataProvider;

    public FareResource() {
        this(new SplitTicketDataProvider());
//        this(CompositeSingletonFareDataProvider.getInstance());
    }

    public FareResource(FareDataProvider fareDataProvider) {
        this.fareDataProvider = fareDataProvider;
    }

    @GET
    @Path("/from/{fromId}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFaresFrom(@PathParam("fromId") String fromId) throws WebApplicationException {
        try {
            LOG.info("Request to GET fares received, from " + fromId);
            FareSet fareSet = fareDataProvider.getFaresFrom(fromId);
            String json = getGson().toJson(fareSet);
            LOG.info(truncate(json));
            return json;
        } catch (Exception e) {
            LOG.error("Failed to get fares", e);
            throw new WebApplicationException(e, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

}
