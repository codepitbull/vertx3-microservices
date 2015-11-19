package de.codepitbull.vertx.async;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import rx.Observable;

import static rx.observables.JoinObservable.from;
import static rx.observables.JoinObservable.when;

public class HystrixVerticle extends AbstractVerticle {

    public static final String ADDRESS_HYSTRIX = "microservices.hystrix";
    public static final String ADDRESS_EXEC = "microservices.exec";

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        Observable<Void> blockingObs = vertx.eventBus().<Integer>consumer(ADDRESS_EXEC)
                .handler(msg ->
                        vertx.<Integer>executeBlocking(
                                future -> future.complete(add(msg.body())),
                                result -> msg.reply(result.result())
                        ))
                .completionHandlerObservable();

        Observable<Void> hystrixObs = vertx.eventBus().<Integer>consumer(ADDRESS_HYSTRIX)
                .handler(msg -> new BlockingHystrixCommand(msg.body())
                        .observe()
                        .subscribe(val -> vertx.runOnContext(v -> msg.reply(val))))
                .completionHandlerObservable();

        when(
                from(hystrixObs)
                        .and(blockingObs)
                        .then((a, b) -> null)
        ).toObservable().subscribe(
                success -> startFuture.complete(),
                failure -> startFuture.fail(failure)
        );
    }

    private class BlockingHystrixCommand extends HystrixCommand<Integer> {
        private Integer add;

        public BlockingHystrixCommand(Integer add) {
            super(HystrixCommandGroupKey.Factory.asKey("blocking"));
            this.add = add;
        }

        @Override
        protected Integer run() throws Exception {
            return add(add);
        }
    }

    private Integer add(Integer add) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return add + 1;
    }
}
