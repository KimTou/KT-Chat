package cn.tojintao.rpc.impl;

import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.rpc.PushService;
import cn.tojintao.service.MsgService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cjt
 * @date 2023/1/6 21:26
 */
@DubboService
@Service
public class PushServiceImpl implements PushService {

    @Autowired
    private MsgService msgService;

    @Override
    public void pushMsg(MessageVo messageVo) {
        msgService.push(messageVo.getReceiver(), messageVo);
    }

    @Override
    public void pushGroupMsg(GroupMessage groupMessage) {
        List<Integer> userIdList = groupMessage.getUserIdList();
        for (Integer receiverId : userIdList) {
            msgService.push(receiverId, groupMessage);
        }
    }
}
