package cn.tojintao.service;

import cn.tojintao.constant.MsgConstant;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author cjt
 * @date 2022/6/20 19:59
 */
@RocketMQMessageListener(consumerGroup = MsgConstant.GROUP_MSG_SAVE_TOPIC, topic = MsgConstant.GROUP_MSG_SAVE_TOPIC)
@Component
public class GroupMessageListener implements RocketMQListener<MessageExt> {

    @Autowired
    private ChatService chatService;

    @Override
    public void onMessage(MessageExt messageExt) {
        String json = new String(messageExt.getBody());
        GroupMessage groupMessage = JSON.parseObject(json, GroupMessage.class);
        chatService.saveGroupMessage(groupMessage);
    }
}
