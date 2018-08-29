package edu.demo2;

import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import test2.PcInput;
import test2.PcOutput;
import test2.TestService2Grpc;

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
    private final TestService2Grpc.TestService2BlockingStub blockingStub;

    public TestClient(String host, int port) {
        channel = ManagedChannelBuilder.forAddress(host, port)
                // Channels are secure by default (via SSL/TLS). For the example we disable TLS to avoid
                // needing certificates.
                .usePlaintext(true)
                .build();
        // 使用我们从proto文件生成的GreeterGrpc类提供的newBlockingStub方法指定channel创建stubs
        blockingStub = TestService2Grpc.newBlockingStub(channel);
    }

    public static void main(String[] args) throws InterruptedException {
        TestClient client = new TestClient("localhost", 50052);
        try {
            //调用对应的方法
            client.test("name1", "20", "sex1");
        } finally {
            client.shutdown();
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    public void test(String name, String age, String sex) {
        PcInput request = PcInput.newBuilder().setName(name).setAge(age).setSex(sex).build();
        PcOutput response;
        try {
            Channel channel =  ManagedChannelBuilder.forAddress("localhost", 50052).usePlaintext(true).build();
            TestService2Grpc.TestService2BlockingStub blockingStub =TestService2Grpc.newBlockingStub(channel);
            //调用方法
            response = blockingStub.testFunction2(request);
        } catch (StatusRuntimeException e) {
            return;
        }
        logger.info("name: " + response.getName() + ",age: " + response.getAge() + ",sex: " + response.getSex());
    }
}
