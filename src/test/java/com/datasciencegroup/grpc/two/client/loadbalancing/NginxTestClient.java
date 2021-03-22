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
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8585)
                .usePlaintext()
                .build();
        this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);

    }

    @Test
    public void balanceTest() throws InterruptedException {

        for (int i = 0; i < 11; i++) {

            BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
                    .setAccountNumber(i)
                    .build();
            Balance balance = this.blockingStub.getBalance(balanceCheckRequest);

            System.out.println(
                    "\n\tBalance : " + balance.getAmount() + " for Account Number " + balanceCheckRequest.getAccountNumber()

            );
        }
    }
}
