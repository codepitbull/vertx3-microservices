package de.codepitbull.vertx.async;

import co.paralleluniverse.fibers.Suspendable;
import io.vertx.core.Future;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.ext.sync.HandlerReceiverAdaptor;
import io.vertx.ext.sync.SyncVerticle;

import static io.vertx.ext.sync.Sync.awaitResult;
import static io.vertx.ext.sync.Sync.streamAdaptor;

public class SyncExampleVerticle extends SyncVerticle {
    public static final String ADDRESS_SYNC = "microservices.sync";

    @Override
    @Suspendable
    public void start() throws Exception {
        HandlerReceiverAdaptor<Message<String>> adaptor = streamAdaptor();
        vertx.eventBus().<String>consumer(ADDRESS_SYNC).handler(adaptor);

        while (true) {
            Message<String> received = adaptor.receive();
            received.reply("I got: " + received.body());
        }
    }

}
