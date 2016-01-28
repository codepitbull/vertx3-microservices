package de.codepitbull.vertx.async;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import io.vertx.core.Future;
import io.vertx.rxjava.core.AbstractVerticle;
import io.vertx.rxjava.core.Context;
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


        Context ctx = vertx.getOrCreateContext();
        Observable<Void> hystrixObs = vertx.eventBus().<Integer>consumer(ADDRESS_HYSTRIX)
                .handler(msg -> new BlockingHystrixCommand(msg.body())
                        .observe()
                        .subscribe(val -> ctx.runOnContext(v -> msg.reply(val))))
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

        private Setter setter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("blocking"))
                .andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter()
                                .withCircuitBreakerEnabled(true)
                                .withCircuitBreakerErrorThresholdPercentage(5)
                                .withExecutionTimeoutEnabled(true)
                                .withExecutionTimeoutInMilliseconds(100)
                );

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
