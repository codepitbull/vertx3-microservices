package de.codepitbull.vertx.microlith;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

public class MainVerticle extends AbstractVerticle{
    @Override
    public void start() throws Exception {
        vertx.deployVerticle(CalculationVerticle.class.getName());
        vertx.deployVerticle(Resource1Verticle.class.getName());
    }
}
