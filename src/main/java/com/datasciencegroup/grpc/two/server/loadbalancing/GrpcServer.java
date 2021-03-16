package com.datasciencegroup.grpc.two.server.loadbalancing;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(6565)
                .addService(new BankService())
                .build();

        server.start();
        System.out.println("\n\tserver started.....");
        server.awaitTermination();

    }
}
