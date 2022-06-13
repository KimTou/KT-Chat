package cn.tojintao.config;

import cn.tojintao.constant.MsgConstant;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.netty.ChatHandler;
import cn.tojintao.netty.UserChannelRelation;
import cn.tojintao.feign.UserInfoService;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author cjt
 * @date 2022/6/11 23:32
 */
@Slf4j
@Configuration
public class RocketMQConfig {
    @Value("${rocketmq.namesrv.address}")
    private String nameServerAddr;
    @Autowired
    private UserInfoService userInfoService;

    @Value("${netty.connector-url}")
    private String connectorUrl;

    @Bean("msgProducer")
    public DefaultMQProducer msgProducer() throws Exception {
        DefaultMQProducer producer = new DefaultMQProducer(MsgConstant.MSG_GROUP);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    /**
     * 私聊消息消费者（只订阅本机有关的topic）
     */
    @Bean("msgConsumer")
    public DefaultMQPushConsumer msgConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MsgConstant.MSG_GROUP);
        consumer.setNamesrvAddr(nameServerAddr);
        String url = this.connectorUrl;
        consumer.subscribe(MsgConstant.MSG_TOPIC + url, "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                if (msg == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(msg.getBody());
                MessageVo messageVo = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), MessageVo.class);
                System.out.println("私聊消息消费:" + messageVo);
                Integer receiverId = messageVo.getReceiver();
                Channel channel = UserChannelRelation.getChannel(receiverId);
                if (channel != null) {
                    Channel receiverChannel = ChatHandler.userClients.find(channel.id());
                    if (receiverChannel != null) {  //判断目标用户是否在线
                        receiverChannel.writeAndFlush(new TextWebSocketFrame(bodyStr));
                    } else {
                        System.out.println("离线消息:" + messageVo);
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

    /**
     * 群聊消息消费者
     */
    @Bean("groupMsgConsumer")
    public DefaultMQPushConsumer groupMsgConsumer() throws Exception {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(MsgConstant.MSG_GROUP);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(MsgConstant.GROUP_MSG_TOPIC, "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                MessageExt msg = msgs.get(0);
                if (msg == null) {
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(msg.getBody());
                GroupMessage groupMessage = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), GroupMessage.class);
                System.out.println("群聊消息消费:" + groupMessage);
                Integer groupId = groupMessage.getGroupId();
                List<Integer> groupUser = userInfoService.getGroupUser(groupId).getData();
                for (Integer receiverId : groupUser) {
                    Channel channel = UserChannelRelation.getChannel(receiverId);
                    if (channel != null) {
                        Channel receiverChannel = ChatHandler.userClients.find(channel.id());
                        if (receiverChannel != null) {  //判断目标用户是否在线
                            receiverChannel.writeAndFlush(new TextWebSocketFrame(bodyStr));
                        } else {
                            System.out.println("离线消息:" + groupMessage);
                        }
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
