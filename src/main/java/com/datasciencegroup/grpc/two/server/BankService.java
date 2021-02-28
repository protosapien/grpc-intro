package com.datasciencegroup.grpc.two.server;

import com.datasciencegroup.grpc.two.models.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    @Override
    public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

        int accountNumber = request.getAccountNumber();

        // we arbitrarily and artificially set the amount based acct numb.
        // Totally bogus but will do for now
        Balance balance = Balance.newBuilder()
                .setAmount(AccountDatabase.getBalance(accountNumber))
                .build();

        responseObserver.onNext(balance);
        responseObserver.onCompleted();

    }

    // Server-streaming service to withdraw money from the account.
    @Override
    public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {

        int accountNumber = request.getAccountNumber();
        int amount = request.getAmount(); // only in $10 increments; $10, $20, $30...
        int balance = AccountDatabase.getBalance(accountNumber);

        if(balance < amount){
            Status status = Status.FAILED_PRECONDITION.withDescription("Insufficient balance.  You only have $" + balance);
            responseObserver.onError(status.asRuntimeException());
            // we do not want to proceed any further, so we just return
            return;
        }

        // all validation have passed successfully
        for (int i = 0; i < (amount/10); i++) {
            Money money = Money.newBuilder().setValue(10).build();
            responseObserver.onNext(money);
            // but we need to deduct $10 each time we send $10 from the account
            AccountDatabase.deductBalance(accountNumber,10);
        }

        responseObserver.onCompleted();

    }

    @Override
    public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {
        return new CashStreamingRequest(responseObserver);
    }

}
