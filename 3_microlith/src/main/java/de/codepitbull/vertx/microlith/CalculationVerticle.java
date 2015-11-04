package de.codepitbull.vertx.microlith;

import io.vertx.core.Future;


public class CalculationVerticle extends io.vertx.rxjava.core.AbstractVerticle {


    public static final String ADDRESS_CALCULATION = "calculation";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        vertx.eventBus().consumer(ADDRESS_CALCULATION)
                .handler(msg -> msg.reply("from ("+hashCode()+") "+System.currentTimeMillis()))
                .completionHandler(result -> {
                    if (result.succeeded())
                        startFuture.complete();
                    else
                        startFuture.fail(result.cause());
                });
    }
}
