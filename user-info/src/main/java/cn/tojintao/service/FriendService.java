package cn.tojintao.service;

import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.UserVo;

import java.util.List;

/**
 * @author cjt
 * @date 2021/6/21 0:15
 */
public interface FriendService {

    ResultInfo<List<UserVo>> findAllFriend(Integer userId);

    ResultInfo<?> addFriend(Integer userId, String addUserName);

    ResultInfo<List<User>> getRequest(Integer userId);

    ResultInfo<?> agreeRequest(Integer userId, Integer addId);

    ResultInfo<?> refuseRequest(Integer userId, Integer addId);

    ResultInfo<?> deleteFriend(Integer userId, Integer friendId);

}
