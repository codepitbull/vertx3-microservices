package de.codepitbull.vertx.microlith;

import de.codepitbull.vertx.etcd.EtcdRegistration;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

import static de.codepitbull.vertx.microlith.CalculationVerticle.ADDRESS_CALCULATION;

/**
 * Created by jmader on 31.08.15.
 */
public class Resource1Verticle extends AbstractVerticle {

    public static final String CONFIG_PORT = "port";
    public static final String SERVICENAME = "service1";

    private Integer port;
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        port = config().getInteger(CONFIG_PORT, 8000);

        Router router = Router.router(vertx);
        router.get("/"+SERVICENAME+"/*").failureHandler(fail -> {
            System.out.println("FAILED");
        }).handler(req -> {
            vertx.eventBus().<Long>send(ADDRESS_CALCULATION, 1, reply -> {
                req.response().end("service1 -> timestamp: " + reply.result().body());
                req.response().close();
            });
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(port, res -> {
            EtcdRegistration.builder()
                    .vertx(vertx)
                    .etcdHost("127.0.0.1")
                    .etcdPort(4001)
                    .ttl(20)
                    .servicename(SERVICENAME)
                    .serviceHost("127.0.0.1")
                    .servicePort(port)
                    .nodename("mynode")
                    .build().connect(etcdRes -> {
                if (etcdRes.succeeded())
                    startFuture.complete();
                else
                    startFuture.fail(etcdRes.cause());
            });
        });
    }
}
