package com.datasciencegroup.grpc.two.client;

import com.datasciencegroup.grpc.two.models.Balance;
import com.google.gson.JsonParseException;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.CountDownLatch;

public class BalanceStreamObserver implements StreamObserver<Balance> {

    private CountDownLatch latch;

    public BalanceStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onNext(Balance balance) {
        System.out.println(
                "\n\tFinal Balance : $" + balance.getAmount()
        );
    }

    @Override
    public void onError(Throwable throwable) {
        this.latch.countDown();
    }

    // in this case this is a call-back
    @Override
    public void onCompleted() {
        System.out.println(
                "\n\tServer is finished!"
        );

        this.latch.countDown();
    }
}
