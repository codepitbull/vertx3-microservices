package de.codepitbull.vertx.microservice;

import io.vertx.core.Vertx;
import org.apache.commons.lang3.mutable.MutableInt;

/**
 * Created by jmader on 09.08.15.
 */
public class PingMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(MicroserviceVerticle.class.getName());
    }
}
