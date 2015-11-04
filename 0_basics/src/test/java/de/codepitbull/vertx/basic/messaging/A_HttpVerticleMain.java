package de.codepitbull.vertx.basic.messaging;

import de.codepitbull.vertx.basic.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;


public class A_HttpVerticleMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(4));
    }
}
