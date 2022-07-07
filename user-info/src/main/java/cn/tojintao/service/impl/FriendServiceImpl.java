package cn.tojintao.service.impl;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.mapper.FriendMapper;
import cn.tojintao.mapper.UserMapper;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.UserVo;
import cn.tojintao.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
        friendMapper.deleteRequest(userId, addId);
        return ResultInfo.success(CodeEnum.SUCCESS);
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
        return ResultInfo.success(CodeEnum.SUCCESS);
    }
}
