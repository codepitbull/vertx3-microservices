package de.codepitbull.vertx.microlith;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * Created by jmader on 31.08.15.
 */
public class CalculationVerticle extends AbstractVerticle{


    public static final String ADDRESS_CALCULATION = "calculation";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(ADDRESS_CALCULATION)
                .handler(msg -> msg.reply(System.currentTimeMillis()))
                .completionHandler(result -> {
                    if (result.succeeded())
                        startFuture.complete();
                    else
                        startFuture.fail(result.cause());
                });
    }
}
