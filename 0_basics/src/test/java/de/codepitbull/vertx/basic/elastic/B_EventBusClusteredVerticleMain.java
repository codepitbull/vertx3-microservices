package de.codepitbull.vertx.basic.elastic;

import de.codepitbull.vertx.basic.EventBusVerticle;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;


public class B_EventBusClusteredVerticleMain {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), res ->
            res.result().deployVerticle(EventBusVerticle.class.getName()));
    }
}
