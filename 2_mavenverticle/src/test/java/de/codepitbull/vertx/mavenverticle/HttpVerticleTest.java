package de.codepitbull.vertx.mavenverticle;

import de.codepitbull.vertx.mavenverticle.HttpVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.RunTestOnContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(VertxUnitRunner.class)
public class HttpVerticleTest {
    @Rule
    public final RunTestOnContext rule = new RunTestOnContext();

    @Before
    public void setUp(TestContext ctx) {
        rule.vertx().deployVerticle(HttpVerticle.class.getName(), new DeploymentOptions().setInstances(4), ctx.asyncAssertSuccess());
    }

    @Test
    public void testInstances(TestContext ctx) {
    }
}
