package com.cracknellj.fare.ws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.net.URI;
import java.util.Date;

public class Main {
    // Base URI the Grizzly HTTP server will listen on
    private final static Logger LOG = LogManager.getLogger(Main.class);
    public static String baseUri = null;

    public static String getBaseURI() {
        return baseUri;
    }

    public static void setServerAddress(String address) {
        baseUri = "http://" + address + ":7926/api/";
    }

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.cracknellj package
        final ResourceConfig rc = new ResourceConfig().packages("com.cracknellj");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at baseUri
        LOG.info("Creating HTTP Server with base uri " + baseUri);
        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUri), rc);

        final HttpHandler httpHandler = new CLStaticHttpHandler(HttpServer.class.getClassLoader(), "/fare/");
        httpServer.getServerConfiguration().addHttpHandler(httpHandler, "/fare/");

        return httpServer;
    }

    public static void main(String[] args) throws Exception {
        setServerAddress("localhost");
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", baseUri));

        while (true) {
            System.out.println((new Date()).toString() + " Web server still running");
            Thread.sleep(600000);
        }
    }
}

