package com.datasciencegroup.grpc.two.client.loadbalancing;

import com.datasciencegroup.grpc.two.models.Balance;
import com.datasciencegroup.grpc.two.models.BalanceCheckRequest;
import com.datasciencegroup.grpc.two.models.BankServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NginxTestClient {


    // make this an instance variable
    private BankServiceGrpc.BankServiceBlockingStub blockingStub;

    @BeforeAll
    public void setup() {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("192.168.1.146", 8585)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);

    }

    @Test
    public void balanceTest() throws InterruptedException {

        System.out.println("\n\tResponse Output.....\n");

        for (int i = 1; i < 5001; i++) {

            BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(i)
                    .build();
            Balance balance = this.blockingStub.getBalance(balanceCheckRequest);

            System.out.println(
                    "\tBalance is " + balance.getAmount() + " for Account Number " + balanceCheckRequest.getAccountNumber()
            );
        }
    }
}
