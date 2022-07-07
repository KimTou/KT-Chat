package cn.tojintao.mq;

import cn.tojintao.constant.MsgConstant;
import cn.tojintao.feign.UserInfoService;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.netty.ChatHandler;
import cn.tojintao.netty.UserChannelRelation;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author cjt
 * @date 2022/6/20 20:08
 */
@RocketMQMessageListener(consumerGroup = MsgConstant.GROUP_MSG_GROUP, topic = MsgConstant.GROUP_MSG_TOPIC)
@Component
public class GroupMessageListener implements RocketMQListener<MessageExt> {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String json = new String(messageExt.getBody());
        GroupMessage groupMessage = JSONObject.toJavaObject(JSONObject.parseObject(json), GroupMessage.class);
        System.out.println("群聊消息消费:" + groupMessage);
        Integer groupId = groupMessage.getGroupId();
        List<Integer> groupUser = userInfoService.getGroupUser(groupId).getData();
        for (Integer receiverId : groupUser) {
            if (receiverId.equals(groupMessage.getSender())) continue;
            Channel channel = UserChannelRelation.getChannel(receiverId);
            if (channel != null) {
                Channel receiverChannel = ChatHandler.userClients.find(channel.id());
                if (receiverChannel != null) {  //判断目标用户是否在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(json));
                } else {
                    System.out.println("离线消息:" + groupMessage);
                }
            }
        }
    }
}
