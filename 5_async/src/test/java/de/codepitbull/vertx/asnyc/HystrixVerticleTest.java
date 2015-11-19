package de.codepitbull.vertx.asnyc;

import de.codepitbull.vertx.async.HystrixVerticle;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static de.codepitbull.vertx.async.HystrixVerticle.ADDRESS_EXEC;
import static de.codepitbull.vertx.async.HystrixVerticle.ADDRESS_HYSTRIX;

@RunWith(VertxUnitRunner.class)
public class HystrixVerticleTest {
    @Rule
    public final RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setUp(TestContext ctx) {
        rule.vertx().deployVerticle(HystrixVerticle.class.getName(), ctx.asyncAssertSuccess());
    }

    @Test
    public void testAddViaHystrix(TestContext ctx) {
        Async async = ctx.async();
        rule.vertx().eventBus().send(ADDRESS_HYSTRIX, 12, rsp -> async.complete());
    }

    @Test
    public void testAddViaExecuteBlocking(TestContext ctx) {
        Async async = ctx.async();
        rule.vertx().eventBus().send(ADDRESS_EXEC, 12, rsp -> async.complete());
    }
}
