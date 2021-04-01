package com.datasciencegroup.grpc.two.client.loadbalancing;

import com.datasciencegroup.grpc.two.client.rpctypes.BalanceStreamObserver;
import com.datasciencegroup.grpc.two.models.Balance;
import com.datasciencegroup.grpc.two.models.BalanceCheckRequest;
import com.datasciencegroup.grpc.two.models.BankServiceGrpc;
import com.datasciencegroup.grpc.two.models.DepositRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NginxTestClient {

    // make this an instance variable
    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("192.168.1.146", 8585)
                .usePlaintext()
                .build();

        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);

    }

    @Test
    public void balanceTest() throws InterruptedException {

        System.out.println("\n\tResponse Output.....\n");

        for (int i = 1; i < 10001; i++) {

            BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(ThreadLocalRandom.current().nextInt(1, 10001))
                    .build();

            Balance balance = this.blockingStub.getBalance(balanceCheckRequest);

            System.out.println(
                    "\tBalance is " + balance.getAmount() + " for Account Number " + balanceCheckRequest.getAccountNumber()
            );
        }
    }

    @Test
    public void cashStreamingRequest() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest> streamObserver
                = this.bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));

        for (int i = 0; i < 100; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder()
                    .setAccountNumber(4)
                    .setAmount(10)
                    .build();
            streamObserver.onNext(depositRequest);
        }
        streamObserver.onCompleted();
        latch.await();
    }
}
