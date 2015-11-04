package de.codepitbull.vertx.microlith2;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class MainVerticle extends AbstractVerticle{
    @Override
    public void start() throws Exception {
        vertx.deployVerticle(Resource2Verticle.class.getName());
    }
}
