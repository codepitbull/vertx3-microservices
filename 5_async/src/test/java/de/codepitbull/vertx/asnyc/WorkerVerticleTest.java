package de.codepitbull.vertx.asnyc;

import de.codepitbull.vertx.async.WorkerVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static de.codepitbull.vertx.async.WorkerVerticle.ADDRESS_WORKER;

@RunWith(VertxUnitRunner.class)
public class WorkerVerticleTest {
    @Rule
    public final RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setUp(TestContext ctx) {
        rule.vertx().deployVerticle(WorkerVerticle.class.getName(), new DeploymentOptions().setWorker(true), ctx.asyncAssertSuccess());
    }

    @Test
    public void testBlockingAdd(TestContext ctx) {
        Async async = ctx.async();
        rule.vertx().eventBus().<Integer>send(ADDRESS_WORKER, 12, rsp -> {
            if (rsp.failed())
                rsp.cause().printStackTrace();
            ctx.assertEquals(13, rsp.result().body());
            async.complete();
        });
    }
}
