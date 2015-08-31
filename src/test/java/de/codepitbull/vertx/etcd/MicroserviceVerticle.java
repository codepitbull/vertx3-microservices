package de.codepitbull.vertx.etcd;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.ext.web.Router;

/**
 * Created by jmader on 09.08.15.
 */
public class MicroserviceVerticle extends AbstractVerticle {

    public static final int PORT = 8666;
    public static final String SERVICENAME = "servicename";
    public static final String HELLO_WORLD = "Hello World!";
    private EtcdRegistration etcdRegistration;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        Router router = Router.router(vertx);
        router.get("/"+SERVICENAME).handler(req -> {
            req.response().end(HELLO_WORLD);
            //Needed because of "Connection reset by peer" caused by HAProxy
            req.response().close();
        });
        vertx.createHttpServer().requestHandler(router::accept).listen(PORT, httpRes -> {
            if(httpRes.failed())
                startFuture.fail(httpRes.cause());
            else {
                etcdRegistration = EtcdRegistration.builder()
                        .vertx(vertx)
                        .etcdHost("127.0.0.1")
                        .etcdPort(4001)
                        .ttl(20)
                        .servicename(SERVICENAME)
                        .serviceHost("127.0.0.1")
                        .servicePort(PORT)
                        .nodename("mynode")
                        .build();

                etcdRegistration.connect(etcdRes -> {
                    if (etcdRes.succeeded())
                        startFuture.complete();
                    else
                        startFuture.fail(etcdRes.cause());
                });
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        etcdRegistration.disconnect(stopFuture);
    }
}
