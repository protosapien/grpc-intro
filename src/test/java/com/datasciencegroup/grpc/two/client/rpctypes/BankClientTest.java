package com.datasciencegroup.grpc.two.client.rpctypes;

import com.datasciencegroup.grpc.two.models.*;
import com.datasciencegroup.grpc.two.server.rpctypes.MoneyStreamingResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.concurrent.CountDownLatch;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {

    // make this an instance variable
    private BankServiceGrpc.BankServiceBlockingStub blockingStub;
    private BankServiceGrpc.BankServiceStub bankServiceStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("192.168.1.146", 6565)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
        this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);

    }

    @Test
    public void balanceTest() throws InterruptedException {

        BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                .setAccountNumber(5)
                .build();
        Balance balance = this.blockingStub.getBalance(balanceCheckRequest);

        System.out.println(
                "\n\tBalance : " + balance.getAmount() + " for Account Number " + balanceCheckRequest.getAccountNumber()
        );
    }

    // Adding this for Section 4, Lecture 56
    @Test
    public void withdrawTest() {

        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
                .setAccountNumber(7)
                .setAmount(40)
                .build();

        // this call waits for the response from the server
        this.blockingStub.withdraw(withdrawRequest)
                .forEachRemaining(money -> System.out.println("\n\tReceived : $" + money.getValue()));
    }

    @Test
    public void withdrawAsyncTest() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder().setAccountNumber(5).setAmount(50).build();
        this.bankServiceStub.withdraw(withdrawRequest, new MoneyStreamingResponse(latch));
        latch.await();
        // Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
    }

    @Test
    public void cashStreamingRequest() throws InterruptedException {

        CountDownLatch latch = new CountDownLatch(1);
        StreamObserver<DepositRequest> streamObserver
                = this.bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));

        for (int i = 0; i < 10; i++) {
            DepositRequest depositRequest = DepositRequest.newBuilder()
                    .setAccountNumber(5)
                    .setAmount(10)
                    .build();
            streamObserver.onNext(depositRequest);
        }
        streamObserver.onCompleted();
        latch.await();
    }
}
