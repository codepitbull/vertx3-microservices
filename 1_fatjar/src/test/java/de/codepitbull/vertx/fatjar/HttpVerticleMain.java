package de.codepitbull.vertx.fatjar;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by jmader on 31.08.15.
 */
public class HttpVerticleMain {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(HttpVerticle.class.getName());
//        Vertx.clusteredVertx(new VertxOptions(), res -> {
//            res.result().deployVerticle(HttpVerticle.class.getName());
//        });
    }
}
