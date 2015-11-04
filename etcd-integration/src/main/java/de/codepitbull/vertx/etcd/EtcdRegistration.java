package de.codepitbull.vertx.etcd;

import io.vertx.core.AsyncResultHandler;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.vertx.core.http.HttpHeaders.APPLICATION_X_WWW_FORM_URLENCODED;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Created by jmader on 10.08.15.
 */
public class EtcdRegistration {

    private final static Logger LOG = LoggerFactory.getLogger(EtcdRegistration.class);

    private static final List<Integer> SUCCESS_CODES = Collections.unmodifiableList(Arrays.asList(200, 201, 403));

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

    public void connect(AsyncResultHandler<Void> asyncResultHandler) {
        connectToEtcd(vertx, httpClient, servicename, nodename, ttl, hostAndPort, asyncResultHandler);
    }

    public void disconnect(Future<Void> stopFuture) {
        deleteInstanceNode(httpClient, servicename, nodename, res -> {
            if (res.statusCode() == 200) {
                stopFuture.complete();
                LOG.info("Successfully unregistered " + nodename);
            } else {
                stopFuture.fail("(" + res.statusCode() + ") => " + res.statusMessage());
                LOG.error("Failed unregistering " + nodename + " (" + res.statusCode() + ") " + res.statusMessage());
            }
        });
    }

    private static void connectToEtcd(Vertx vertx, HttpClient httpClient, String servicename, String nodename, int ttl, String hostAndPort, AsyncResultHandler<Void> asyncResultHandler) {

        createServiceNode(httpClient, servicename,
                pathCreated -> {
                    //403 means the directory already existed
                    if (SUCCESS_CODES.contains(pathCreated.statusCode()))
                        createInstanceNode(httpClient, servicename, nodename, "value=" + hostAndPort + "&ttl=" + ttl,
                                nodeCreated -> {
                                    if (SUCCESS_CODES.contains(nodeCreated.statusCode()) || 403 == nodeCreated.statusCode()) {
                                        startNodeRefresh(vertx, httpClient, servicename, nodename, ttl, hostAndPort, asyncResultHandler);
                                    }
                                    else {
                                        LOG.error("Unable to create node (" + nodeCreated.statusCode() + ") " + nodeCreated.statusMessage());
                                        asyncResultHandler.handle(Future.factory.completedFuture("Unable to create node node (" + nodeCreated.statusCode() + ") " + nodeCreated.statusMessage(), true));
                                    }

                                });
                    else {
                        LOG.error("Unable to create service node (" + pathCreated.statusCode() + ") " + pathCreated.statusMessage());
                        asyncResultHandler.handle(Future.factory.completedFuture("Unable to create service node (" + pathCreated.statusCode() + ") " + pathCreated.statusMessage(), true));
                    }
                });
    }

    private static void startNodeRefresh(Vertx vertx, HttpClient httpClient, String servicename, String nodename, int ttl, String hostAndPort, AsyncResultHandler<Void> asyncResultHandler) {
        LOG.info("Succeeded registering");
        vertx.setPeriodic((ttl - 4) * 1000,
                refresh -> createInstanceNode(httpClient, servicename, nodename, "value=" + hostAndPort + "&ttl=" + ttl, refreshed -> {
                    if (refreshed.statusCode() != 200) {
                        LOG.error("Unable to refresh node (" + refreshed.statusCode() + ") " + refreshed.statusMessage());
                    }
                }));
        asyncResultHandler.handle(Future.factory.completedFuture(null));
    }

    public static void createServiceNode(HttpClient client, String serviceName, Handler<HttpClientResponse> responseHandler) {
        client
                .put(ETCD_BASE_PATH + serviceName)
                .putHeader(CONTENT_TYPE.toString(), APPLICATION_X_WWW_FORM_URLENCODED.toString())
                .handler(responseHandler)
                .end("dir=true");
    }

    public static void createInstanceNode(HttpClient client, String serviceName, String name, String data, Handler<HttpClientResponse> responseHandler) {
        client
                .put(ETCD_BASE_PATH + serviceName + "/" +name)
                .putHeader(CONTENT_TYPE.toString(), APPLICATION_X_WWW_FORM_URLENCODED.toString())
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
        private String serviceHost;
        private Integer servicePort;

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

        public Builder serviceHost(String serviceHost) {
            this.serviceHost = serviceHost;
            return this;
        }

        public Builder servicePort(Integer servicePort) {
            this.servicePort = servicePort;
            return this;
        }

        public EtcdRegistration build() {
            notNull(vertx, "vertx must not be null");
            notNull(etcdHost, "etcdHost must not be null");
            notNull(etcdPort, "etcdPort must not be null");
            notNull(ttl, "ttl must not be null");
            notNull(servicename, "servicename must not be null");
            notNull(serviceHost, "serviceHost must not be null");
            notNull(servicePort, "servicePort must not be null");
            notNull(nodename, "nodename must not be null");
            return new EtcdRegistration(vertx, etcdHost, etcdPort, ttl, servicename, nodename, serviceHost+":"+servicePort);
        }

    }
}
