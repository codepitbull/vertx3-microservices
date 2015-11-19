package de.codepitbull.vertx.asnyc;

import de.codepitbull.vertx.async.SyncExampleVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static de.codepitbull.vertx.async.SyncExampleVerticle.ADDRESS_SYNC;

@RunWith(VertxUnitRunner.class)
public class SyncExampleVerticleTest {
    @Rule
    public final RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setUp(TestContext ctx) {
        rule.vertx().deployVerticle(SyncExampleVerticle.class.getName());
    }

    @Test
    public void testHello(TestContext ctx) {
        Async async = ctx.async();
        rule.vertx().timerStream(2000).handler(t -> {
            rule.vertx().eventBus().send(ADDRESS_SYNC, "hello", new DeliveryOptions().setSendTimeout(10l),
                    rsp -> {
                        ctx.assertEquals("I got: hello", rsp.result().body());
                        async.complete();
                    }
            );
        });
    }
}
