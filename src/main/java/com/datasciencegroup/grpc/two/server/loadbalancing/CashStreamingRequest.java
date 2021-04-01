package com.datasciencegroup.grpc.two.server.loadbalancing;

import com.datasciencegroup.grpc.two.models.Balance;
import com.datasciencegroup.grpc.two.models.DepositRequest;
import com.datasciencegroup.grpc.two.server.rpctypes.AccountDatabase;
import io.grpc.stub.StreamObserver;

public class CashStreamingRequest implements StreamObserver<DepositRequest> {

    private StreamObserver<Balance> balanceStreamObserver;
    private int accountBalance;

    public CashStreamingRequest(StreamObserver<Balance> balanceStreamObserver) {
        this.balanceStreamObserver = balanceStreamObserver;
    }

    @Override
    public void onNext(DepositRequest depositRequest) {
        int accountNumber = depositRequest.getAccountNumber();
        int amount = depositRequest.getAmount();

        System.out.println(
                "\tReceived cash deposit of " + amount + " from Account " + accountNumber
        );

        this.accountBalance = AccountDatabase.addBalance(accountNumber, amount);
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(
                throwable.getMessage()
        );
    }

    @Override
    public void onCompleted() {
        Balance balance = Balance.newBuilder()
                .setAmount(this.accountBalance)
                .build();
        this.balanceStreamObserver.onNext(balance);
        this.balanceStreamObserver.onCompleted();
    }
}
