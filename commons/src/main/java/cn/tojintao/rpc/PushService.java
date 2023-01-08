package cn.tojintao.rpc;

import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.vo.MessageVo;

/**
 * @author cjt
 * @date 2023/1/6 21:23
 */
public interface PushService {

    void pushMsg(MessageVo messageVo);

    void pushGroupMsg(GroupMessage groupMessage);
}
