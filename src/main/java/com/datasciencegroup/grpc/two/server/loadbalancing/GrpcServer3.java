package com.datasciencegroup.grpc.two.server.loadbalancing;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

public class GrpcServer3 {

    public static void main(String[] args) throws IOException, InterruptedException {

        Server server = ServerBuilder.forPort(4545)
                .addService(new BankService())
                .build();

        server.start();
        System.out.println("\n\tserver 3 started.....");
        server.awaitTermination();

    }
}
