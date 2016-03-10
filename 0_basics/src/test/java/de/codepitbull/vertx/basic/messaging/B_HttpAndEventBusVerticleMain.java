package de.codepitbull.vertx.basic.messaging;

import de.codepitbull.vertx.basic.EventBusVerticle;
import de.codepitbull.vertx.basic.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;


public class B_HttpAndEventBusVerticleMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(4));

        vertx.deployVerticle(EventBusVerticle.class.getName());
    }
}
