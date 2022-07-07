package cn.tojintao.mq;

import cn.tojintao.constant.MsgConstant;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.netty.ChatHandler;
import cn.tojintao.netty.UserChannelRelation;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * @author cjt
 * @date 2022/6/20 19:47
 */
@RocketMQMessageListener(consumerGroup = MsgConstant.MSG_GROUP, topic = MsgConstant.MSG_TOPIC, selectorExpression = "${netty.port}")
@Component
public class MessageListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt messageExt) {
        String json = new String(messageExt.getBody());
        MessageVo messageVo = JSONObject.toJavaObject(JSONObject.parseObject(json), MessageVo.class);
        System.out.println("私聊消息消费:" + messageVo);
        Integer receiverId = messageVo.getReceiver();
        Channel channel = UserChannelRelation.getChannel(receiverId);
        if (channel != null) {
            Channel receiverChannel = ChatHandler.userClients.find(channel.id());
            if (receiverChannel != null) {  //判断目标用户是否在线
                receiverChannel.writeAndFlush(new TextWebSocketFrame(json));
            } else {
                System.out.println("离线消息:" + messageVo);
            }
        }
    }
}
