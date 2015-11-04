package de.codepitbull.vertx.mavenverticle;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;


public class HttpVerticleMain {
    public static void main(String[] args) {
        Vertx.vertx().deployVerticle("maven:de.codepitbull.vertx.microservice:mavenverticle:1.0-SNAPSHOT::service-verticle",
                res -> {
                    System.out.println("Deployed "+res.succeeded());
                });
    }
}
