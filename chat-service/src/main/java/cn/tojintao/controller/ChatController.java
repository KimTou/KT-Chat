package cn.tojintao.controller;

import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import cn.tojintao.model.vo.BoxVo;
import cn.tojintao.model.vo.MessageVo;
import cn.tojintao.service.ChatService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author cjt
 * @date 2022/5/8 19:56
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @ApiOperation("获取聊天记录")
    @GetMapping("/getChatById")
    public ResultInfo<List<MessageVo>> getChatById(Integer userId, Integer friendId) {
        return chatService.getChatById(userId, friendId);
    }

    @ApiOperation("获取群聊天记录")
    @GetMapping("/getGroupChatById")
    public ResultInfo<List<GroupMessage>> getGroupChatById(Integer groupId) {
        return chatService.getGroupChatById(groupId);
    }

    @ApiOperation("删除聊天记录")
    @PostMapping("/deleteChat")
    public ResultInfo<?> deleteChat(Integer userId, Integer friendId) {
        return chatService.deleteChat(userId, friendId);
    }

    @ApiOperation("获取所有聊天框")
    @GetMapping("/getAllChatBox")
    public ResultInfo<List<BoxVo>> getAllChatBox(Integer userId) {
        return chatService.getAllChatBox(userId);
    }

    @ApiOperation("获取所有群聊")
    @GetMapping("/getAllGroup")
    public ResultInfo<List<Group>> getAllGroup(Integer userId) {
        return chatService.getAllGroup(userId);
    }

    @ApiOperation("新建群聊")
    @PostMapping("/addGroup")
    public ResultInfo<?> addGroup(Integer userId, String groupName) {
        return chatService.addGroup(userId, groupName);
    }

    @ApiOperation("退出群聊")
    @PostMapping("/outGroup")
    public ResultInfo<?> outGroup(Integer userId, Integer groupId) {
        return chatService.outGroup(userId, groupId);
    }

    @ApiOperation("加入群聊")
    @PostMapping("/intoGroup")
    public ResultInfo<?> intoGroup(Integer userId, String groupName) {
        return chatService.intoGroup(userId, groupName);
    }

    @ApiOperation("模糊查询历史消息")
    @GetMapping("/searchMessage")
    public ResultInfo<List<Map<String, Object>>> searchMessage(Integer userId, String keyword) throws IOException {
        return chatService.searchMessage(userId, keyword);
    }

    @PostMapping("/saveMessage")
    public ResultInfo<Message> saveMessage(@RequestBody Message message) {
        return chatService.saveMessage(message);
    }

    @PostMapping("/saveGroupMessage")
    public ResultInfo<GroupMessage> saveGroupMessage(@RequestBody GroupMessage groupMessage) {
        return chatService.saveGroupMessage(groupMessage);
    }
}