package de.codepitbull.vertx.basic;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.dropwizard.MetricsService;
import io.vertx.ext.web.Router;

import static de.codepitbull.vertx.basic.EventBusVerticle.ADDRESS_REQUEST_COUNT;

public class HttpVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.get("/serv1").handler(req -> {
            vertx.eventBus().send(ADDRESS_REQUEST_COUNT, 1,
                    res -> {
                        if (res.failed())
                            req.response().end("I am " + hashCode() +" and nobody is answering");
                        else
                            req.response().end("I am " + hashCode() +" and I got "+res.result().body());
                    });
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8000, res -> {
            if (res.succeeded())
                startFuture.complete();
            else
                startFuture.fail(res.cause());
        });

    }
}
