package com.datasciencegroup.grpc.two.server;

import com.datasciencegroup.grpc.two.models.TransferRequest;
import com.datasciencegroup.grpc.two.models.TransferResponse;
import com.datasciencegroup.grpc.two.models.TransferServiceGrpc;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceGrpc.TransferServiceImplBase {


    // this will return a TransferRequest object - so we need to implement this class
    @Override
    public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {
        return new TransferStreamingRequest(responseObserver);
    }
}
