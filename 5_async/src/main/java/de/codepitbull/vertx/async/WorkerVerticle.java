package de.codepitbull.vertx.async;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;

import static org.apache.commons.lang3.Validate.isTrue;

public class WorkerVerticle extends AbstractVerticle {
    public static final String ADDRESS_WORKER = "microservices.worker";

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        isTrue(vertx.getOrCreateContext().isWorkerContext(), WorkerVerticle.class.getName() + " msut be deployed as Worker!");
        vertx.eventBus().consumer(ADDRESS_WORKER, this::consume).completionHandler(cpl -> startFuture.complete());
    }

    private void consume(Message<Integer> msg) {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupted();
        }
        msg.reply(msg.body() + 1);
    }

}
