package de.codepitbull.vertx.fatjar;

import io.vertx.core.Vertx;


public class HttpVerticleMain {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(HttpVerticle.class.getName());
    }
}
