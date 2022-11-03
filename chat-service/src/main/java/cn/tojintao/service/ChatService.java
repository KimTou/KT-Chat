package cn.tojintao.service;

import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import cn.tojintao.model.vo.BoxVo;
import cn.tojintao.model.vo.MessageVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author cjt
 * @date 2021/6/20 20:20
 */
public interface ChatService {

    ResultInfo<List<MessageVo>> getChatById(Integer userId, Integer friendId);

    ResultInfo<List<GroupMessage>> getGroupChatById(Integer groupId);

    ResultInfo<Message> saveMessage(Message message);

    ResultInfo<MessageVo> saveMessageVo(MessageVo messageVo);

    ResultInfo<GroupMessage> saveGroupMessage(GroupMessage groupMessage);

    ResultInfo<?> deleteChat(Integer userId, Integer friendId);

    ResultInfo<List<BoxVo>> getAllChatBox(Integer userId);

    ResultInfo<List<Group>> getAllGroup(Integer userId);

    ResultInfo<?> addGroup(Integer userId, String groupName);

    ResultInfo<?> outGroup(Integer userId, Integer groupId);

    ResultInfo<?> intoGroup(Integer userId, String groupName);

    ResultInfo<List<Map<String, Object>>> searchMessage(Integer userId, String keyword) throws IOException;
}
