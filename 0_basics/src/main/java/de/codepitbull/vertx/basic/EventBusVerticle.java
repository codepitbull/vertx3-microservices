package de.codepitbull.vertx.basic;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;


public class EventBusVerticle extends AbstractVerticle {

    private final Logger LOG = LoggerFactory.getLogger(EventBusVerticle.class);

    public static final String ADDRESS_REQUEST_COUNT = "request.count";

    private Integer counter = 0;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().<Integer>consumer(ADDRESS_REQUEST_COUNT).handler(msg -> {
            counter += msg.body();
            LOG.info("COUNT: "+counter);
            msg.reply(counter+" "+hashCode());
        });
    }
}
