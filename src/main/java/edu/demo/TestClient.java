package edu.demo;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import test.TestServiceGrpc;
import test.testProto;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @Description
 * @Author xiashuai01
 * @Create 2018/8/28 下午5:05
 */
public class TestClient {

    private static final Logger logger = Logger.getLogger(TestClient.class.getName());

    private final ManagedChannel channel;
    private final TestServiceGrpc.TestServiceBlockingStub blockingStub;

    public TestClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true)
                .build();
        // 使用我们从proto文件生成的GreeterGrpc类提供的newBlockingStub方法指定channel创建stubs
        blockingStub = TestServiceGrpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws InterruptedException {
        TestClient client = new TestClient("localhost", 50051);
        try {
            String user = "大司马";
            //调用对应的方法
            client.test(user);
        } finally {
            client.shutdown();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void test(String name) {
        testProto.TestInput request = testProto.TestInput.newBuilder().setKey(name).build();
        testProto.TestOutput response;
        try {
            Channel channel =  ManagedChannelBuilder.forAddress("localhost", 50051).usePlaintext(true).build();
            TestServiceGrpc.TestServiceBlockingStub blockingStub =TestServiceGrpc.newBlockingStub(channel);
            //调用方法
            response = blockingStub.testFunction(request);
        } catch (StatusRuntimeException e) {
            return;
        }
        logger.info("Greeting: " + response.getKey());
    }
}
