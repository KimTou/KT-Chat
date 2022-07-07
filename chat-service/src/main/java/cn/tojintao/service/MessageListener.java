package cn.tojintao.service;

import cn.tojintao.constant.MsgConstant;
import cn.tojintao.model.entity.Message;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cjt
 * @date 2022/6/20 19:03
 */
@RocketMQMessageListener(consumerGroup = MsgConstant.MSG_SAVE_GROUP, topic = MsgConstant.MSG_SAVE_TOPIC)
@Component
public class MessageListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ChatService chatService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String json = new String(messageExt.getBody());
        Message message = JSON.parseObject(json, Message.class);
        chatService.saveMessage(message);
    }
}
