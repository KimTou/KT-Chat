package cn.tojintao.service.impl;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.exception.ConditionException;
import cn.tojintao.mapper.UserMapper;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.User;
import cn.tojintao.service.UserService;
import cn.tojintao.util.TokenUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author cjt
 * @date 2021/6/19 16:48
 */
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String BAN_USER = "ban_user:";

    /**
     * 登录
     */
    @Override
    public ResultInfo<JSONObject> login(String userName, String password) throws Exception {
        if (userName.length() == 0 || password.length() == 0){
            return ResultInfo.error(400, "请填写完整信息");
        }
        User user = userMapper.login(userName, password);
        if (user == null){
            return ResultInfo.error(400, "用户名或密码错误");
        }
        Integer userId = user.getUserId();
        String accessToken = TokenUtil.generateAccessToken(userId);
        String refreshToken = TokenUtil.generateRefreshToken(userId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", refreshToken);
        jsonObject.put("user", user);
        return ResultInfo.success(CodeEnum.SUCCESS, jsonObject);
    }

    @Override
    public ResultInfo<JSONObject> refreshAccessToken(String refreshToken) throws Exception {
        Integer userId = TokenUtil.verifyToken(refreshToken);
        User user = userMapper.getUserById(userId);
        if (user == null) {
            throw new ConditionException("认证token失败");
        }
        //更新双token
        String accessToken = TokenUtil.generateAccessToken(userId);
        String newRefreshToken = TokenUtil.generateRefreshToken(userId);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", newRefreshToken);
        return ResultInfo.success(CodeEnum.SUCCESS, jsonObject);
    }

    @Override
    public ResultInfo<User> findUserById(Integer userId) {
        User user = userMapper.getUserById(userId);
        return ResultInfo.success(CodeEnum.SUCCESS, user);
    }

    @Override
    public ResultInfo<List<User>> getUserByIdList(List<Integer> userIdList) {
        List<User> userList = userMapper.getUserByIdList(userIdList);
        return ResultInfo.success(CodeEnum.SUCCESS, userList);
    }

    @Override
    public ResultInfo<List<Group>> getAllGroup(Integer userId) {
        List<Group> allGroup = userMapper.getAllGroup(userId);
        return ResultInfo.success(CodeEnum.SUCCESS, allGroup);
    }

    @Override
    public ResultInfo<List<Integer>> getGroupUser(Integer groupId) {
        List<Integer> groupUserIdList = userMapper.getGroupUser(groupId);
        return ResultInfo.success(CodeEnum.SUCCESS, groupUserIdList);
    }

    @Override
    public ResultInfo<Group> getGroupById(Integer groupId) {
        Group group = userMapper.getGroupById(groupId);
        return ResultInfo.success(CodeEnum.SUCCESS, group);
    }

    @Override
    public ResultInfo<String> isBan(Integer userId) {
        Boolean result = redisTemplate.hasKey(BAN_USER + userId);
        if (Boolean.TRUE.equals(result)) {
            return ResultInfo.error(CodeEnum.FORBIDDEN);
        } else {
            return ResultInfo.success(CodeEnum.SUCCESS);
        }
    }
}
