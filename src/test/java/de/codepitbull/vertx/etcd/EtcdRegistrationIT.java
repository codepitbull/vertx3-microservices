package de.codepitbull.vertx.etcd;

import io.vertx.core.http.HttpClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static de.codepitbull.vertx.etcd.MicroserviceVerticle.HELLO_WORLD;
import static de.codepitbull.vertx.etcd.MicroserviceVerticle.PORT;
import static de.codepitbull.vertx.etcd.MicroserviceVerticle.SERVICENAME;


/**
 * Test requires running instances of HAProxy and ETCD!
 */
@RunWith(VertxUnitRunner.class)
public class EtcdRegistrationIT {
    public static final int HAPROXY_REFRESH_INTERVAL = 5000;
    public static final int PROXIED_PORT = 7777;
    public static final String HOST = "127.0.0.1";
    @Rule
    public final RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setUp(TestContext ctx) {
        rule.vertx().deployVerticle(MicroserviceVerticle.class.getName(), ctx.asyncAssertSuccess());
    }

    @Test
    public void testHttp(TestContext ctx) {
        HttpClient httpClient = rule.vertx().createHttpClient();
        String serviceUrl = "/"+SERVICENAME;
        Async async = ctx.async();
        httpClient.get(PORT, HOST, serviceUrl).handler(response -> {
            response.bodyHandler(body -> {
                ctx.assertEquals(HELLO_WORLD, body.toString());
                rule.vertx().setTimer(HAPROXY_REFRESH_INTERVAL, timer -> {
                    httpClient.get(PROXIED_PORT, HOST, serviceUrl).handler(proxyResponse -> {
                        proxyResponse.bodyHandler(proxyBody -> {
                            ctx.assertEquals(HELLO_WORLD, proxyBody.toString());
                            async.complete();
                        });
                    }).end();
                });
            });
        }).end();
    }
}
