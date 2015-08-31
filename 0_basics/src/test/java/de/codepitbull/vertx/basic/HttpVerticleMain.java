package de.codepitbull.vertx.basic;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

/**
 * Created by jmader on 31.08.15.
 */
public class HttpVerticleMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpVerticle.class.getName());
    }
}
