package cn.tojintao.service;

import cn.tojintao.constant.MsgConstant;
import cn.tojintao.feign.UserInfoService;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.netty.ChatHandler;
import cn.tojintao.netty.UserChannelRelation;
import cn.tojintao.util.DateUtil;
import com.alibaba.fastjson.JSON;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author cjt
 * @date 2022/6/11 23:57
 */
@Component
public class MsgService {
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisService redisService;
    @Resource
    private RocketMQTemplate template;
    @Resource
    private RestTemplate restTemplate;

    @Value("${netty.connector-url}")
    public String connectorUrl;
    private static final String PREFIX = "http://";

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
        //封装页面展示对象
        MessageVo messageVo = new MessageVo();
        messageVo.setId(message.getId());
        messageVo.setSender(message.getSender());
        messageVo.setReceiver(message.getReceiver());
        messageVo.setContent(message.getContent());
        messageVo.setGmtCreate(message.getGmtCreate());
        messageVo.setUserName(user.getUserName());
        messageVo.setAvatar(user.getAvatar());

        //消息异步入库
        template.convertAndSend(MsgConstant.MSG_SAVE_TOPIC, messageVo);
        //chatService.saveMessage(message);

        //消息转发
        transfer(receiverId, messageVo);
    }

    /**
     * 私聊消息转发
     * @param receiverId 接收用户id
     * @param messageVo 私聊消息
     */
    public void transfer(Integer receiverId, MessageVo messageVo) {
        String receiverUrl = redisService.getConnectorUrl(receiverId);
        if (receiverUrl == null) {
            System.out.println("离线消息:" + messageVo);
        } else if (this.connectorUrl.equals(receiverUrl)) { //判断目标用户是否在本机
            //在本机: 直接推送
            push(receiverId, messageVo);
        } else {
            //不在本机: 转发消息
            receiverUrl = PREFIX + receiverUrl.replace("_", ":") + "/push/pushMsg";
            restTemplate.postForObject(receiverUrl, messageVo, String.class);
        }
    }

    /**
     * 消息推送
     */
    public void push(Integer receiverId, MessageVo messageVo) {
        Channel channel = UserChannelRelation.getChannel(receiverId);
        if (channel != null) {
            Channel receiverChannel = ChatHandler.userClients.find(channel.id());
            if (receiverChannel != null) {  //判断目标用户是否在线
                receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(messageVo)));
            } else {
                System.out.println("离线消息:" + messageVo);
            }
        }
    }

    /**
     * 群聊
     */
    public void sendGroupMessage(Integer senderId, Integer groupId, String msg) throws IOException {
        User user = userInfoService.findUserById(senderId).getData();
        Group group = userInfoService.getGroupById(groupId).getData();
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroupId(groupId);
        groupMessage.setGroupName(group.getGroupName());
        groupMessage.setSender(senderId);
        groupMessage.setUserName(user.getUserName());
        groupMessage.setAvatar(user.getAvatar());
        groupMessage.setContent(msg);
        groupMessage.setGmtCreate(DateUtil.getDate());

        //消息异步入库
        template.convertAndSend(MsgConstant.GROUP_MSG_SAVE_TOPIC, groupMessage);
        //chatService.saveGroupMessage(groupMessage);

        List<Integer> userIdList = userInfoService.getGroupUser(groupId).getData();
        userIdList.remove(senderId);
        groupMessage.setUserIdList(userIdList);

        //群聊消息转发
        pushGroupUser(userIdList, groupMessage);
    }

    /**
     * 群聊消息转发
     * @param userIdList 接收群组用户id
     * @param groupMessage 群聊消息
     */
    public void pushGroupUser(List<Integer> userIdList, GroupMessage groupMessage) {
        Set<String> targetUrlSet = new HashSet<>();
        for (Integer receiverId : userIdList) {
            String receiverUrl = redisService.getConnectorUrl(receiverId);
            if (receiverUrl == null) {
                System.out.println("离线消息:" + groupMessage);
            } else if (this.connectorUrl.equals(receiverUrl)) { //判断目标用户是否在本机
                //在本机: 直接推送
                push(receiverId, groupMessage);
            } else {
                //不在本机: 转发消息
                targetUrlSet.add(receiverUrl);
            }
        }
        transfer(targetUrlSet, groupMessage);
    }

    public void transfer(Set<String> targetUrlSet, GroupMessage groupMessage) {
        for (String receiverUrl : targetUrlSet) {
            receiverUrl = PREFIX + receiverUrl.replace("_", ":") + "/push/pushGroupMsg";
            restTemplate.postForObject(receiverUrl, groupMessage, String.class);
        }
    }

    public void push(Integer receiverId, GroupMessage groupMessage) {
        Channel channel = UserChannelRelation.getChannel(receiverId);
        if (channel != null) {
            Channel receiverChannel = ChatHandler.userClients.find(channel.id());
            if (receiverChannel != null) {  //判断目标用户是否在线
                receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(groupMessage)));
            }
        }
    }
}
