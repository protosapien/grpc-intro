package com.datasciencegroup.grpc.two.server.rpctypes;

import com.datasciencegroup.grpc.two.models.Account;
import com.datasciencegroup.grpc.two.models.TransferRequest;
import com.datasciencegroup.grpc.two.models.TransferResponse;
import com.datasciencegroup.grpc.two.models.TransferStatus;
import io.grpc.stub.StreamObserver;

public class TransferStreamingRequest implements StreamObserver<TransferRequest> {

    // we will be able to use this for streaming the responses to the client
    private StreamObserver<TransferResponse> transferResponseStreamObserver;

    // CTOR
    public TransferStreamingRequest(StreamObserver<TransferResponse> transferResponseStreamObserver) {
        this.transferResponseStreamObserver = transferResponseStreamObserver;
    }

    @Override
    public void onNext(TransferRequest transferRequest) {

        int fromAccount = transferRequest.getFromAccount();
        int toAccount = transferRequest.getToAccount();
        int amount = transferRequest.getAmount();

        // we don't care about the balance of the toAccount since we're transferring to the toAcct
        int balance = AccountDatabase.getBalance(fromAccount);

        // we just set this to the default
        TransferStatus status = TransferStatus.FAILED;

        if (balance >= amount && fromAccount != toAccount) {
            // this transaction can proceed but need to do this as an ACID transaction
            AccountDatabase.deductBalance(fromAccount, amount);
            AccountDatabase.addBalance(toAccount, amount);

            // if this transaction succeeds
            status = TransferStatus.SUCCESS;
        }

        // now we need to send the transfer response
        Account fromAccountInfo = Account.newBuilder().setAccountNumber(fromAccount).setAmount(AccountDatabase.getBalance(fromAccount)).build();
        Account toAccountInfo = Account.newBuilder().setAccountNumber(toAccount).setAmount(AccountDatabase.getBalance(toAccount)).build();
        TransferResponse transferResponse = TransferResponse.newBuilder()
                .setStatus(status)
                .addAccounts(fromAccountInfo)
                .addAccounts(toAccountInfo)
                .build();

        // for each request, we will send a response -> bi-directional streaming!
        this.transferResponseStreamObserver.onNext(transferResponse);

    }

    @Override
    public void onError(Throwable throwable) {

        System.out.println(
                "\n\tAn error occurred.  Here's the current status of the database: "
        );

        AccountDatabase.printAccountDetails();

    }

    // after all the response messages have been sent onCompleted() will be invoked
    // on this class, so we need  to invoke the onCompleted() for the transfer response stream
    @Override
    public void onCompleted() {
        // we print out the contents of the database just see it's state
        AccountDatabase.printAccountDetails();
        this.transferResponseStreamObserver.onCompleted();
    }

}
