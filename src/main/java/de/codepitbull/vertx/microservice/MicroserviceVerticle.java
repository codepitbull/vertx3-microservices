package de.codepitbull.vertx.microservice;

import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

/**
 * Created by jmader on 09.08.15.
 */
public class MicroserviceVerticle extends AbstractVerticle {


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        EtcdRegistration.builder()
                .vertx(vertx)
                .etcdHost("127.0.0.1")
                .etcdPort(4001)
                .ttl(20)
                .servicename("servicename")
                .hostAndPort("127.0.0.1:8080")
                .nodename("mynode")
                .build()
                .start(res -> {
                    if(res.succeeded())
                        startFuture.succeeded();
                    else
                        startFuture.fail(res.cause());
                });
    }




}
