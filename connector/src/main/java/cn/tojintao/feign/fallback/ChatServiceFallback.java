package cn.tojintao.feign.fallback;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.feign.ChatService;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import org.springframework.stereotype.Component;

/**
 * @author cjt
 * @date 2022/6/20 17:03
 */
@Component
public class ChatServiceFallback implements ChatService {
    @Override
    public ResultInfo<Message> saveMessage(Message message) {
        return new ResultInfo<>(500, "远程调用失败");
    }

    @Override
    public ResultInfo<GroupMessage> saveGroupMessage(GroupMessage groupMessage) {
        return new ResultInfo<>(500, "远程调用失败");
    }
}
