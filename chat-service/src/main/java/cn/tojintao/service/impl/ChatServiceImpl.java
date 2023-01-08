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
import cn.tojintao.repository.MessageRepository;
import cn.tojintao.repository.entity.MessageEntity;
import cn.tojintao.service.ChatService;
import cn.tojintao.service.SnowflakeService;
import cn.tojintao.feign.UserInfoService;
import com.alibaba.fastjson.JSON;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


/**
 * @author cjt
 * @date 2021/6/20 20:20
 */
@Log4j
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
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

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
                messageVo.setUserName(user.getUserName());
                messageVo.setAvatar(user.getAvatar());
            }else{
                messageVo.setUserName(friend.getUserName());
                messageVo.setAvatar(friend.getAvatar());
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
        List<GroupMessage> groupChatMsg = chatMapper.getGroupChatById(tableNum, groupId);
        return ResultInfo.success(CodeEnum.SUCCESS, groupChatMsg);
    }

    /**
     * 保存聊天记录
     * @param message
     * @return
     */
    @Override
    public ResultInfo<Message> saveMessage(Message message) {
        message.setId(snowflakeService.getId().getId()); //生成分布式id
        int tableNum = (message.getSender() + message.getReceiver()) % messageCount;
        chatMapper.saveMessage(tableNum, message);
        return ResultInfo.success(CodeEnum.SUCCESS, message);
    }

    @Override
    public ResultInfo<MessageVo> saveMessageVo(MessageVo messageVo) {
        messageVo.setId(snowflakeService.getId().getId()); //生成分布式id
        int tableNum = (messageVo.getSender() + messageVo.getReceiver()) % messageCount;
        chatMapper.saveMessage(tableNum, messageVo);

        MessageEntity messageEntity = JSON.parseObject(JSON.toJSONString(messageVo), MessageEntity.class);
        messageRepository.save(messageEntity); //双写至ES（更好的方式是利用canal异步同步至ES）
        return ResultInfo.success(CodeEnum.SUCCESS, messageVo);
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

        MessageEntity messageEntity = JSON.parseObject(JSON.toJSONString(groupMessage), MessageEntity.class);
        messageRepository.save(messageEntity); //双写至ES
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
            Boolean status = redisTemplate.opsForSet().isMember(ONLINE_USER,
                    String.valueOf(userVo.getUser().getUserId()));
            userVo.setStatus(Boolean.TRUE.equals(status));
            BoxVo boxVo = new BoxVo();
            boxVo.setUserVo(userVo);
            boxVo.setType(false);
            boxVoList.add(boxVo);
        }
        //获取群聊
        List<Group> allGroup = getAllGroup(userId).getData();
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
        if(StringUtils.isBlank(groupName)){
            return ResultInfo.error(CodeEnum.NULL_PARAM, "群聊名不能为空");
        }
        //群聊不能同名
        if(chatMapper.findGroupByName(groupName) != null){
            return ResultInfo.error(CodeEnum.BAD_REQUEST, "群组已存在");
        }
        Group group = new Group();
        group.setGroupName(groupName);
        chatMapper.insertGroup(group);
        chatMapper.intoGroup(userId, group.getGroupId());
        return ResultInfo.success(CodeEnum.SUCCESS, "新建成功");
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
        return ResultInfo.success(CodeEnum.SUCCESS, "退出成功");
    }

    /**
     * 加入群聊
     * @param userId
     * @param groupName
     * @return
     */
    @Override
    public ResultInfo<?> intoGroup(Integer userId, String groupName) {
        if(StringUtils.isBlank(groupName)){
            return ResultInfo.error(CodeEnum.NULL_PARAM, "群聊名不能为空");
        }
        Group group = chatMapper.findGroupByName(groupName);
        if (group == null){
            return ResultInfo.error(CodeEnum.BAD_REQUEST, "群组不存在");
        }
        List<Integer> userGroupIds = chatMapper.getUserGroupIds(userId);
        if (userGroupIds.contains(group.getGroupId())) {
            return ResultInfo.error(CodeEnum.BAD_REQUEST, "已加入该群聊，无需重复添加");
        }
        chatMapper.intoGroup(userId, group.getGroupId());
        return ResultInfo.success(CodeEnum.SUCCESS, "加入成功");
    }

    @Override
    public ResultInfo<List<Map<String, Object>>> searchMessage(Integer userId, String keyword) throws IOException {
        if (StringUtils.isBlank(keyword)) return ResultInfo.error(CodeEnum.NULL_PARAM);
        if (keyword.length() > 100) return ResultInfo.error(CodeEnum.TOO_LONG);

        List<Group> allGroup = userInfoService.getAllGroup(userId).getData();
        List<Integer> groupIds = allGroup.stream().map(Group::getGroupId).collect(Collectors.toList());

        SearchRequest searchRequest = new SearchRequest("messages");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        MultiMatchQueryBuilder matchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "content");
        TermQueryBuilder tq1 = QueryBuilders.termQuery("sender", userId);
        TermQueryBuilder tq2 = QueryBuilders.termQuery("receiver", userId);
        boolQueryBuilder.must(matchQueryBuilder).should(tq1).should(tq2);
        for (Integer groupId : groupIds) {
            boolQueryBuilder.should(QueryBuilders.termQuery("groupId", groupId));
        }
        sourceBuilder.query(boolQueryBuilder);
        sourceBuilder.sort("gmtCreate", SortOrder.DESC);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest);
        List<Map<String, Object>> res = new ArrayList<>();
        for (SearchHit document : searchResponse.getHits().getHits()) {
            log.info(document);
            res.add(document.getSourceAsMap());
        }
        return ResultInfo.success(CodeEnum.SUCCESS, res);
    }
}
