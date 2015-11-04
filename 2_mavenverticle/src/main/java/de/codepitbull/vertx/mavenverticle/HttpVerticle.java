package de.codepitbull.vertx.mavenverticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;


public class HttpVerticle extends AbstractVerticle {
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.get("/service").handler(req -> req.response().end("Ich wurde als Maven Verticle gestartet."));
        vertx.createHttpServer().requestHandler(router::accept).listen(8000, res -> startFuture.complete());
    }
}
