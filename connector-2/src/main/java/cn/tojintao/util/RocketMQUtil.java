package cn.tojintao.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author cjt
 * @date 2022/6/12 0:05
 */
@Slf4j
public class RocketMQUtil {

    public static void syncSendMsg(DefaultMQProducer producer, String topic, String jsonStr) throws Exception{
        Message msg = new Message(topic, jsonStr.getBytes(StandardCharsets.UTF_8));
        SendResult result = producer.send(msg);
        log.info(result.toString());
    }

    public static void asyncSendMsg(DefaultMQProducer producer, String topic, String jsonStr) throws Exception{
        Message msg = new Message(topic, jsonStr.getBytes(StandardCharsets.UTF_8));
        producer.send(msg, new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println(sendResult.getMsgId());
            }
            @Override
            public void onException(Throwable e) {
                System.out.println("发送消息时发生了异常！" + e);
                e.printStackTrace();
            }
        });
    }
}
