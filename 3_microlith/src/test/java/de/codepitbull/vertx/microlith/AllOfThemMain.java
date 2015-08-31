package de.codepitbull.vertx.microlith;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by jmader on 01.09.15.
 */
public class AllOfThemMain {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), res -> {
            res.result().deployVerticle(CalculationVerticle.class.getName());
            res.result().deployVerticle(Resource1Verticle.class.getName());
            res.result().deployVerticle(Resource2Verticle.class.getName());
        });
    }
}
