package cn.tojintao.service.impl;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.mapper.ChatMapper;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.BoxVo;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.model.vo.UserVo;
import cn.tojintao.service.ChatService;
import cn.tojintao.service.SnowflakeService;
import cn.tojintao.feign.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;


/**
 * @author cjt
 * @date 2021/6/20 20:20
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private SnowflakeService snowflakeService;
    private static final String ONLINE_USER = "online_user";

    private static final int messageCount = 3;

    /**
     * 获取聊天记录
     */
    @Override
    public ResultInfo<List<MessageVo>> getChatById(Integer userId, Integer friendId) {
        int tableNum = (userId + friendId) % messageCount;
        List<MessageVo> messages = chatMapper.getChatById(tableNum, userId, friendId);
        //获取两者的详细信息
        User user = userInfoService.findUserById(userId).getData();
        User friend = userInfoService.findUserById(friendId).getData();
        for (MessageVo messageVo : messages) {
            if(messageVo.getSender().equals(userId)){
                messageVo.setSenderName(user.getUserName());
                messageVo.setSenderAvatar(user.getAvatar());
            }else{
                messageVo.setSenderName(friend.getUserName());
                messageVo.setSenderAvatar(friend.getAvatar());
            }
        }
        return ResultInfo.success(CodeEnum.SUCCESS, messages);
    }

    /**
     * 获取群聊天记录
     * @param groupId
     * @return
     */
    @Override
    public ResultInfo<List<GroupMessage>> getGroupChatById(Integer groupId) {
        int tableNum = groupId % messageCount;
        List<GroupMessage> groupChatMes = chatMapper.getGroupChatById(tableNum, groupId);
        return ResultInfo.success(CodeEnum.SUCCESS, groupChatMes);
    }

    /**
     * 保存聊天记录
     * @param message
     * @return
     */
    @Override
    public ResultInfo<Message> saveMessage(Message message) {
        message.setId(snowflakeService.getId().getId());
        int tableNum = (message.getSender() + message.getReceiver()) % messageCount;
        chatMapper.saveMessage(tableNum, message);
        return ResultInfo.success(CodeEnum.SUCCESS, message);
    }


    /**
     * 保存群聊
     * @param groupMessage
     * @return
     */
    @Override
    public ResultInfo<GroupMessage> saveGroupMessage(GroupMessage groupMessage) {
        groupMessage.setId(snowflakeService.getId().getId());
        int tableNum = groupMessage.getGroupId() % messageCount;
        chatMapper.saveGroupMessage(tableNum, groupMessage);
        return ResultInfo.success(CodeEnum.SUCCESS, groupMessage);
    }

    /**
     * 删除聊天记录
     * @param userId
     * @param friendId
     * @return
     */
    @Override
    public ResultInfo<?> deleteChat(Integer userId, Integer friendId) {
        int tableNum = (userId + friendId) % messageCount;
        chatMapper.deleteChat(tableNum, userId, friendId);
        return ResultInfo.success(CodeEnum.SUCCESS);
    }

    /**
     * 获取所有聊天框
     * @param userId
     * @return
     */
    @Override
    public ResultInfo<List<BoxVo>> getAllChatBox(Integer userId) {
        List<BoxVo> boxVoList = new LinkedList<>();
        //获取私聊
        List<UserVo> friendList = userInfoService.findAllFriend(userId).getData();
        for(UserVo userVo : friendList){
            Boolean status = redisTemplate.opsForSet().isMember(ONLINE_USER, String.valueOf(userVo.getUser().getUserId()));
            userVo.setStatus(Boolean.TRUE.equals(status));
            BoxVo boxVo = new BoxVo();
            boxVo.setUserVo(userVo);
            boxVo.setType(false);
            boxVoList.add(boxVo);
        }
        //获取群聊
        List<Group> allGroup = userInfoService.getAllGroup(userId).getData();
        for (Group group : allGroup){
            BoxVo boxVo = new BoxVo();
            boxVo.setGroup(group);
            boxVo.setType(true);
            boxVoList.add(boxVo);
        }
        return ResultInfo.success(CodeEnum.SUCCESS, boxVoList);
    }

    /**
     * 获取所有群聊
     * @param userId
     * @return
     */
    @Override
    public ResultInfo<List<Group>> getAllGroup(Integer userId) {
        List<Group> allGroup = chatMapper.getAllGroup(userId);
        return ResultInfo.success(CodeEnum.SUCCESS, allGroup);
    }

    /**
     * 新建群聊
     * @param userId
     * @param groupName
     * @return
     */
    @Override
    public ResultInfo<?> addGroup(Integer userId, String groupName) {
        if(groupName == null || groupName.length() == 0){
            return ResultInfo.error(CodeEnum.NULL_PARAM);
        }
        //群聊不能同名
        if(chatMapper.findGroupByName(groupName) != null){
            return ResultInfo.error(CodeEnum.BAD_REQUEST);
        }
        Integer groupId = chatMapper.insertGroup(groupName);
        chatMapper.intoGroup(userId, groupId);
        return ResultInfo.success(CodeEnum.SUCCESS);
    }

    /**
     * 退出群聊
     * @param userId
     * @param groupId
     * @return
     */
    @Override
    public ResultInfo<?> outGroup(Integer userId, Integer groupId) {
        chatMapper.outGroup(userId, groupId);
        return ResultInfo.success(CodeEnum.SUCCESS);
    }

    /**
     * 加入群聊
     * @param userId
     * @param groupName
     * @return
     */
    @Override
    public ResultInfo<?> intoGroup(Integer userId, String groupName) {
        if(groupName == null || groupName.length() == 0){
            return ResultInfo.error(CodeEnum.NULL_PARAM);
        }
        Group group = chatMapper.findGroupByName(groupName);
        if(group == null){
            return ResultInfo.error(CodeEnum.BAD_REQUEST);
        }
        chatMapper.intoGroup(userId, group.getGroupId());
        return ResultInfo.success(CodeEnum.SUCCESS);
    }
}
