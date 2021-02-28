package com.datasciencegroup.grpc.two.server;

import com.datasciencegroup.grpc.two.models.Money;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class MoneyStreamingResponse implements StreamObserver<Money> {

    private CountDownLatch latch;

    public MoneyStreamingResponse(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Money money) {
        System.out.println(
                "\n\tReceived async: $" + money.getValue()
        );
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(
                throwable.getMessage()
        );

        latch.countDown();
    }

    @Override
    public void onCompleted() {
        System.out.println(
                "\n\tServer has completed its task!"
        );

        latch.countDown();
    }
}

