package de.codepitbull.vertx.basic.elastic;

import de.codepitbull.vertx.basic.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;


public class A_HttpVerticleClusteredMain {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(),
                res -> res.result().deployVerticle(HttpVerticle.class.getName()));
    }
}
