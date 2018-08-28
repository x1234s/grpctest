package edu.demo;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import test.TestServiceGrpc;
import test.testProto;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @Description
 * @Author xiashuai01
 * @Create 2018/8/28 下午5:05
 */
public class TestServer {

    private static final Logger logger = Logger.getLogger(TestServer.class.getName());
    private int port = 50051;
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final TestServer testServer = new TestServer();
        testServer.start();
        testServer.blockUntilShutdown();
    }

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50051;
        //这个部分启动server
        server = ServerBuilder.forPort(port)
                .addService(new TestImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                TestServer.this.stop();
                System.err.println("*** server shut down");
            }
        });

    }

    private void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    static class TestImpl extends TestServiceGrpc.TestServiceImplBase {

        @Override
        public void testFunction(testProto.TestInput request, StreamObserver<testProto.TestOutput> responseObserver) {
            testProto.TestOutput reply = testProto.TestOutput.newBuilder().setKey("瓜皮" + request.getKey()).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }


}
