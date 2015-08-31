package de.codepitbull.vertx.basic;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by jmader on 31.08.15.
 */
public class EventBusVerticleMain {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), res -> {
            res.result().deployVerticle(HttpVerticle.class.getName());
        });
    }
}
