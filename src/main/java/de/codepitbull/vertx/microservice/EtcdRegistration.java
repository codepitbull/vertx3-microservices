package de.codepitbull.vertx.microservice;

import io.vertx.core.AsyncResult;
import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.impl.FutureFactoryImpl;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.http.HttpClient;
import io.vertx.rxjava.core.http.HttpClientResponse;

import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Created by jmader on 10.08.15.
 */
public class EtcdRegistration {

    private final static Logger LOG = LoggerFactory.getLogger(EtcdRegistration.class);

    private Vertx vertx;
    private int ttl;
    private String servicename;
    private String hostAndPort;
    private HttpClient httpClient;
    private String nodename;

    public static final String ETCD_BASE_PATH = "/v2/keys/";

    private EtcdRegistration(Vertx vertx, String etcdHost, int etcdPort, int ttl, String servicename, String nodename, String hostAndPort) {
        this.vertx = vertx;
        this.ttl = ttl;
        this.nodename = nodename;
        this.servicename = servicename;
        this.hostAndPort = hostAndPort;

        httpClient = vertx.createHttpClient(new HttpClientOptions()
                        .setDefaultHost(etcdHost)
                        .setDefaultPort(etcdPort)
        );
    }

    public void start(AsyncResultHandler<Void> asyncResultHandler) {
        connectToEtcd(vertx, httpClient, servicename, nodename, ttl, hostAndPort, asyncResultHandler);
    }

    public void stop() {
        deleteInstanceNode(httpClient, servicename, nodename, res -> {
            if (res.statusCode() == 201)
                LOG.info("Successfully unregistered " + nodename);
            else
                LOG.error("Failed unregistering " + nodename + " (" + res.statusCode() + ") " + res.statusMessage());
        });
    }

    private static void connectToEtcd(Vertx vertx, HttpClient httpClient, String servicename, String nodename, int ttl, String hostAndPort, AsyncResultHandler<Void> asyncResultHandler) {

        createServiceNode(httpClient, servicename,
                pathCreated -> {
                    //403 means the directory already existed
                    if (pathCreated.statusCode() == 201 || pathCreated.statusCode() == 403)
                        createInstanceNode(httpClient, servicename, nodename, "value=" + hostAndPort + "&ttl=" + ttl,
                                nodeCreated -> {
                                    if (nodeCreated.statusCode() == 201)
                                        vertx.setPeriodic((ttl - 4) * 1000,
                                                refresh -> createInstanceNode(httpClient, servicename, nodename, "value=" + hostAndPort + "&ttl=" + ttl, refreshed -> {
                                                    if (refreshed.statusCode() != 200) {
                                                        LOG.error("Unable to refresh node (" + refreshed.statusCode() + ") " + refreshed.statusMessage());
                                                        asyncResultHandler.handle(Future.factory.completedFuture("Unable refresh node (" + refreshed.statusCode() + ") " + refreshed.statusMessage(), true));
                                                    }
                                                }));
                                    else {
                                        LOG.error("Unable to create node node (" + nodeCreated.statusCode() + ") " + nodeCreated.statusMessage());
                                        asyncResultHandler.handle(Future.factory.completedFuture("Unable to create node node (" + nodeCreated.statusCode() + ") " + nodeCreated.statusMessage(), true));
                                    }

                                });
                    else {
                        LOG.error("Unable to create service node (" + pathCreated.statusCode() + ") " + pathCreated.statusMessage());
                        asyncResultHandler.handle(Future.factory.completedFuture("Unable to create service node (" + pathCreated.statusCode() + ") " + pathCreated.statusMessage(), true));
                    }
                });
    }

    public static void createServiceNode(HttpClient client, String serviceName, Handler<HttpClientResponse> responseHandler) {
        client
                .put(ETCD_BASE_PATH + serviceName)
                .putHeader(CONTENT_TYPE.toString(), HttpHeaders.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .handler(responseHandler)
                .end("dir=true");
    }

    public static void createInstanceNode(HttpClient client, String serviceName, String name, String data, Handler<HttpClientResponse> responseHandler) {
        client
                .put(ETCD_BASE_PATH + serviceName + "/" +name)
                .putHeader(CONTENT_TYPE.toString(), HttpHeaders.APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .handler(responseHandler)
                .end(data);
    }

    public static void deleteInstanceNode(HttpClient client, String serviceName, String name, Handler<HttpClientResponse> responseHandler) {
        client
                .delete(ETCD_BASE_PATH + serviceName + "/" + name)
                .handler(responseHandler)
                .end();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Vertx vertx;
        private String etcdHost;
        private int etcdPort;
        private int ttl;
        private String nodename;
        private String servicename;
        private String hostAndPort;

        public Builder vertx(Vertx vertx) {
            this.vertx = vertx;
            return this;
        }

        public Builder etcdHost(String etcdHost) {
            this.etcdHost = etcdHost;
            return this;
        }

        public Builder etcdPort(int etcdPort) {
            this.etcdPort = etcdPort;
            return this;
        }

        public Builder ttl(int ttl) {
            this.ttl = ttl;
            return this;
        }

        public Builder nodename(String nodename) {
            this.nodename = nodename;
            return this;
        }

        public Builder servicename(String servicename) {
            this.servicename = servicename;
            return this;
        }

        public Builder hostAndPort(String hostAndPort) {
            this.hostAndPort = hostAndPort;
            return this;
        }

        public EtcdRegistration build() {
            notNull(vertx, "vertx must not be null");
            notNull(etcdHost, "etcdHost must not be null");
            notNull(etcdPort, "etcdPort must not be null");
            notNull(ttl, "ttl must not be null");
            notNull(servicename, "servicename must not be null");
            notNull(hostAndPort, "hostAndPort must not be null");
            notNull(nodename, "nodename must not be null");
            return new EtcdRegistration(vertx, etcdHost, etcdPort, ttl, servicename, nodename, hostAndPort);
        }

    }
}
