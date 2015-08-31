package de.codepitbull.vertx.basic;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

import static de.codepitbull.vertx.basic.EventBusVerticle.ADDRESS_REQUEST_COUNT;

/**
 * Created by jmader on 31.08.15.
 */
public class HttpVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.get("/hello").handler(req -> {
            req.response().end("world");
            vertx.eventBus().send(ADDRESS_REQUEST_COUNT, 1);
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(8000, res -> startFuture.complete());
    }
}
