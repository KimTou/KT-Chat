package cn.tojintao.service.impl;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.mapper.FriendMapper;
import cn.tojintao.mapper.UserMapper;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.UserVo;
import cn.tojintao.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cjt
 * @date 2021/6/21 0:15
 */
@Service
public class FriendServiceImpl implements FriendService {

    @Autowired
    private FriendMapper friendMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    private static final String FRIEND_RELATION = "friend:relation:";
    private static final String FRIEND_RECOMMEND = "friend:recommend:";
    private static final String ONLINE_USER = "online_user";

    /**
     * 好友关系-共同好友
     */
    @Override
    public ResultInfo<List<User>> findMutualFriend(Integer userId, Integer friendId) {
        Set<String> intersect = redisTemplate.opsForSet().intersect(FRIEND_RELATION + userId, FRIEND_RELATION + friendId);
        System.out.println("共同好友" + intersect);
        List<Integer> friendIds = intersect.stream().map(Integer::valueOf).collect(Collectors.toList());
        List<User> friends = userMapper.getUserByIdList(friendIds);
        return ResultInfo.success(CodeEnum.SUCCESS, friends);
    }

    /**
     * 好友关系-好友推荐
     */
    @Override
    public ResultInfo<List<User>> findRecommendFriend(Integer userId) {
        if (!redisTemplate.hasKey(FRIEND_RECOMMEND + userId)) {
            List<Integer> allFriendId = friendMapper.findAllFriendId(userId);
            Set<String> difference = new HashSet<>();
            for (Integer friendId : allFriendId) {
                Set<String> set = redisTemplate.opsForSet().difference(FRIEND_RELATION + friendId, FRIEND_RELATION + userId);
                difference.addAll(set);
            }
            redisTemplate.opsForSet().add(FRIEND_RECOMMEND + userId,
                    difference.toArray(new String[difference.size()]));
            redisTemplate.expire(FRIEND_RECOMMEND + userId, 1, TimeUnit.DAYS);
        }
        List<String> objects = redisTemplate.opsForSet().randomMembers(FRIEND_RECOMMEND + userId, 5);
        List<Integer> friendIds = objects.stream().map(Integer::valueOf).collect(Collectors.toList());
        List<User> userList = userMapper.getUserByIdList(friendIds);
        return ResultInfo.success(CodeEnum.SUCCESS, userList);
    }

    /**
     * 获取所有好友
     * @param userId
     * @return
     */
    @Override
    public ResultInfo<List<UserVo>> findAllFriend(Integer userId) {
        List<Integer> allFriend = friendMapper.findAllFriendId(userId);
        List<User> friendList = userMapper.getUserByIdList(allFriend);
        List<UserVo> allFriendVo = new LinkedList<>();
        for(User user: friendList){
            UserVo userVo = new UserVo();
            userVo.setUser(user);
            Boolean status = redisTemplate.opsForSet().isMember(ONLINE_USER, String.valueOf(user.getUserId()));
            userVo.setStatus(Boolean.TRUE.equals(status));
            allFriendVo.add(userVo);
        }
        return ResultInfo.success(CodeEnum.SUCCESS, allFriendVo);
    }

    /**
     * 发送好友添加请求
     * @param userId
     * @param addUserName
     * @return
     */
    @Override
    public ResultInfo<?> addFriend(Integer userId, String addUserName) {
        User addUser = userMapper.getUserByName(addUserName);
        if(addUser == null){
            return ResultInfo.error(CodeEnum.PARAM_NOT_IDEAL, "未查找到该用户");
        }else{
            //判断是否已发送添加好友请求
            if(friendMapper.requestIsExist(userId, addUser.getUserId()) != null){
                return ResultInfo.error(CodeEnum.BAD_REQUEST, "你已发送过添加请求，请等待对方回应");
            }else{
                friendMapper.insertRequest(userId, addUser.getUserId());
                return ResultInfo.success(CodeEnum.SUCCESS);
            }
        }
    }

    /**
     * 获取好友请求
     * @param userId
     * @return
     */
    @Override
    public ResultInfo<List<User>> getRequest(Integer userId) {
        //获取请求用户id
        List<Integer> requestIdList = friendMapper.getRequest(userId);
        List<User> requestUserList = new LinkedList<>();
        for(Integer requestId : requestIdList){
            //获取请求用户对象详细信息
            User requestUser = userMapper.getUserById(requestId);
            requestUserList.add(requestUser);
        }
        return ResultInfo.success(CodeEnum.SUCCESS, requestUserList);
    }

    /**
     * 同意添加好友请求
     * @param userId
     * @param addId
     * @return
     */
    @Override
    public ResultInfo<?> agreeRequest(Integer userId, Integer addId) {
        if(friendMapper.isFriend(userId, addId) != null){
            //判断是否已经是好友关系，是则不再添加
            friendMapper.deleteRequest(userId, addId);
            return ResultInfo.success(CodeEnum.SUCCESS);
        }
        friendMapper.beFriend(userId, addId);
        beFriendToRedis(userId, addId); //同步至Redis
        friendMapper.deleteRequest(userId, addId);
        return ResultInfo.success(CodeEnum.SUCCESS);
    }

    private void beFriendToRedis(Integer userId, Integer addId) {
        redisTemplate.opsForSet().add(FRIEND_RELATION + userId, String.valueOf(addId));
        redisTemplate.opsForSet().add(FRIEND_RELATION + addId, String.valueOf(userId));
    }

    /**
     * 拒绝添加好友
     * @param userId
     * @param addId
     * @return
     */
    @Override
    public ResultInfo<?> refuseRequest(Integer userId, Integer addId) {
        friendMapper.deleteRequest(userId, addId);
        return ResultInfo.success(CodeEnum.SUCCESS);
    }

    /**
     * 删除好友
     * @param userId
     * @param friendId
     * @return
     */
    @Override
    public ResultInfo<?> deleteFriend(Integer userId, Integer friendId) {
        friendMapper.deleteFriend(userId, friendId);
        deleteFriendToRedis(userId, friendId); //同步至Redis
        return ResultInfo.success(CodeEnum.SUCCESS);
    }

    private void deleteFriendToRedis(Integer userId, Integer friendId) {
        redisTemplate.opsForSet().remove(FRIEND_RELATION + userId, String.valueOf(friendId));
        redisTemplate.opsForSet().remove(FRIEND_RELATION + friendId, String.valueOf(userId));
    }
}
