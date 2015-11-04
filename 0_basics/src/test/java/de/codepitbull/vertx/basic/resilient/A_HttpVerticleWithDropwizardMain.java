package de.codepitbull.vertx.basic.resilient;

import de.codepitbull.vertx.basic.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.dropwizard.DropwizardMetricsOptions;
import io.vertx.ext.shell.ShellService;
import io.vertx.ext.shell.ShellServiceOptions;
import io.vertx.ext.shell.net.TelnetOptions;

/**
 * metrics-info vertx.http.servers.0.0.0.0:8000.get-requests
 */
public class A_HttpVerticleWithDropwizardMain {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx(new VertxOptions().setMetricsOptions(
                new DropwizardMetricsOptions()
                        .setJmxEnabled(true)
        ));
        vertx.deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(4));


        ShellService service = ShellService.create(vertx,
                new ShellServiceOptions().setTelnetOptions(
                        new TelnetOptions().
                                setHost("localhost").
                                setPort(4000)
                )
        );
        service.start();
    }
}
