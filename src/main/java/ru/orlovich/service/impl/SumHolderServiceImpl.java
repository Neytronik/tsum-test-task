package ru.orlovich.service.impl;

import io.grpc.stub.StreamObserver;
import ru.orlovich.proto.SumHolderServiceGrpc;
import ru.orlovich.proto.SumHolderServiceOuterClass;
import ru.orlovich.service.RulesResponses;

import java.util.Random;

public class SumHolderServiceImpl extends SumHolderServiceGrpc.SumHolderServiceImplBase {

    private static final RulesResponses rules = new RulesResponses();
    public static int MIN_SLOW_RESPONSE_MILLISECONDS = 5000;
    public static int MAX_SLOW_RESPONSE_MILLISECONDS = 8000;

    @Override
    public void save(SumHolderServiceOuterClass.SumSaveRequest request, StreamObserver<SumHolderServiceOuterClass.SumSaveResponse> responseObserver) {
        slowIntercept(request);
        responseObserver.onNext(rules.applyRulesSaveSum(request));
        responseObserver.onCompleted();
    }

    public void slowIntercept(SumHolderServiceOuterClass.SumSaveRequest request) {
        if (request.getSlow()) {
            try {
                Thread.sleep(new Random().nextInt(MAX_SLOW_RESPONSE_MILLISECONDS - MIN_SLOW_RESPONSE_MILLISECONDS) + MIN_SLOW_RESPONSE_MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
