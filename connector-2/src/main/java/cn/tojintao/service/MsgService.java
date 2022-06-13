package cn.tojintao.service;

import cn.tojintao.constant.MsgConstant;
import cn.tojintao.feign.ChatService;
import cn.tojintao.feign.UserInfoService;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.netty.ChatHandler;
import cn.tojintao.netty.UserChannelRelation;
import cn.tojintao.util.DateUtil;
import cn.tojintao.util.RocketMQUtil;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author cjt
 * @date 2022/6/11 23:57
 */
@Component
public class MsgService {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private RedisService redisService;
    @Resource
    private DefaultMQProducer msgProducer;

    @Value("${netty.connector-url}")
    public String connectorUrl;

    /**
     * 群聊
     */
    public void sendGroupMessage(Integer senderId, Integer groupId, String msg) throws IOException {
        User user = userInfoService.findUserById(senderId).getData();
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroupId(groupId);
        groupMessage.setSender(senderId);
        groupMessage.setUserName(user.getUserName());
        groupMessage.setAvatar(user.getAvatar());
        groupMessage.setContent(msg);
        groupMessage.setGmtCreate(DateUtil.getDate());
        chatService.saveGroupMessage(groupMessage);
        String jsonString = JSON.toJSONString(groupMessage);
        try {
            RocketMQUtil.syncSendMsg(msgProducer, MsgConstant.GROUP_MSG_TOPIC, jsonString);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 私聊
     */
    public void sendMessage(Integer senderId, Integer receiverId, String msg) throws IOException {
        User user = userInfoService.findUserById(senderId).getData();
        Message message = new Message();
        message.setSender(senderId);
        message.setReceiver(receiverId);
        message.setContent(msg);
        message.setGmtCreate(DateUtil.getDate());
        //将消息存入数据库
        chatService.saveMessage(message);
        //封装页面展示对象
        MessageVo messageVo = new MessageVo();
        messageVo.setId(message.getId());
        messageVo.setSender(message.getSender());
        messageVo.setReceiver(message.getReceiver());
        messageVo.setContent(message.getContent());
        messageVo.setGmtCreate(message.getGmtCreate());
        messageVo.setSenderName(user.getUserName());
        messageVo.setSenderAvatar(user.getAvatar());
        String jsonString = JSON.toJSONString(messageVo);
        push(receiverId, jsonString);
    }

    public void push(Integer receiverId, String jsonString) {
        String connectorUrl = redisService.getConnectorUrl(receiverId);
        if (connectorUrl == null) {
            System.out.println("离线消息:" + jsonString);
            return;
        }
        if (this.connectorUrl.equals(connectorUrl)) {  //判断目标用户是否在本机
            Channel channel = UserChannelRelation.getChannel(receiverId);
            if (channel != null) {
                Channel receiverChannel = ChatHandler.userClients.find(channel.id());
                if (receiverChannel != null) {  //判断目标用户是否在线
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(jsonString));
                } else {
                    System.out.println("离线消息:" + jsonString);
                }
            }
        } else {
            try {
                RocketMQUtil.syncSendMsg(msgProducer, MsgConstant.MSG_TOPIC + connectorUrl, jsonString);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
