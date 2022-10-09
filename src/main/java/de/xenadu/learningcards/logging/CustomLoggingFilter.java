package de.xenadu.learningcards.logging;

import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestContext;
import org.jboss.resteasy.reactive.client.spi.ResteasyReactiveClientRequestFilter;

import javax.ws.rs.ext.Provider;

@Provider
public class CustomLoggingFilter implements ResteasyReactiveClientRequestFilter {

    private static final Logger logger = Logger.getLogger(CustomLoggingFilter.class);

    @Override
    public void filter(ResteasyReactiveClientRequestContext requestContext) {
        logger.info("------------------------------");
        logger.infof("Resolved address by Stork: %s", requestContext.getUri().toString());

    }
}
