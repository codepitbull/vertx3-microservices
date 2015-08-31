package de.codepitbull.vertx.microlith;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.json.JsonObject;

import static de.codepitbull.vertx.microlith.Resource2Verticle.CONFIG_PORT;

/**
 * Created by jmader on 01.09.15.
 */
public class OnlyResource2Main {
    public static void main(String[] args) {
        Vertx.clusteredVertx(new VertxOptions(), res -> {
            res.result().deployVerticle(Resource2Verticle.class.getName(),
                    new DeploymentOptions()
                            .setConfig(new JsonObject().put(CONFIG_PORT, 8001)));
        });
    }
}
