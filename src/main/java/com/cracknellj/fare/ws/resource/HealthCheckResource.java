package com.cracknellj.fare.ws.resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "healthcheck" path)
 */
@Path("healthcheck")
public class HealthCheckResource extends AbstractResource {
    private static final Logger LOG = LogManager.getLogger(HealthCheckResource.class);

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        LOG.info("Health check resource accessed");
        return "Got it!";
    }
}
