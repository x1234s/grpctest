package edu.demo2;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import test2.PcInput;
import test2.PcOutput;
import test2.TestService2Grpc;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @Description
 * @Author xiashuai01
 * @Create 2018/8/28 下午5:05
 */
public class TestServer {

    private static final Logger logger = Logger.getLogger(TestServer.class.getName());
    private Server server;

    public static void main(String[] args) throws IOException, InterruptedException {
        final TestServer testServer = new TestServer();
        testServer.start();
        testServer.blockUntilShutdown();
    }

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50052;
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

    static class TestImpl extends TestService2Grpc.TestService2ImplBase {

        @Override
        public void testFunction2(PcInput request, StreamObserver<PcOutput> responseObserver) {
            logger.info("name:"+request.getName()+", age:"+request.getAge()+", sex:" + request.getSex());
           PcOutput reply = test2.PcOutput.newBuilder().setAge(request.getAge() + 2).setName(request.getName() + "good").setSex(request.getSex()).build();
            logger.info("name:"+reply.getName()+", age:"+reply.getAge()+", sex:" + reply.getSex());
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }


}
