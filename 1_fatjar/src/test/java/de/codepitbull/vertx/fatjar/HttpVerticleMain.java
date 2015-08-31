package de.codepitbull.vertx.fatjar;

import io.vertx.core.Vertx;

/**
 * Created by jmader on 31.08.15.
 */
public class HttpVerticleMain {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(HttpVerticle.class.getName());
    }
}
