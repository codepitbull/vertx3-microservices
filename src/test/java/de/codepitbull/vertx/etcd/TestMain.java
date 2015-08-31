package de.codepitbull.vertx.etcd;

import io.vertx.core.Vertx;

/**
 * Created by jmader on 09.08.15.
 */
public class TestMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MicroserviceVerticle.class.getName());
    }
}
