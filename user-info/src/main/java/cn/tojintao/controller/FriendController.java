package cn.tojintao.controller;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.mapper.UserMapper;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.UserVo;
import cn.tojintao.service.FriendService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author cjt
 * @date 2022/5/8 19:42
 */
@RestController
@RequestMapping("/user-info/friend")
public class FriendController {

    @Autowired
    private FriendService friendService;
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/findUserByName")
    public ResultInfo<User> findUserByName(String userName){
        User user = userMapper.getUserByName(userName);
        return ResultInfo.success(CodeEnum.SUCCESS, user);
    }

    @GetMapping("/findAllFriend")
    public ResultInfo<List<UserVo>> findAllFriend(Integer userId) {
        return friendService.findAllFriend(userId);
    }

    @ApiOperation("发送添加好友请求")
    @PostMapping("/addFriend")
    public ResultInfo<?> addFriend(Integer userId, String addUserName) {
        return friendService.addFriend(userId, addUserName);
    }

    @ApiOperation("获取添加好友请求")
    @GetMapping("/getRequest")
    public ResultInfo<List<User>> getRequest(Integer userId) {
        return friendService.getRequest(userId);
    }

    @ApiOperation("同意添加好友请求")
    @PostMapping("/agreeRequest")
    public ResultInfo<?> agreeRequest(Integer userId, Integer addId) {
        return friendService.agreeRequest(userId, addId);
    }

    @ApiOperation("拒绝添加好友请求")
    @PostMapping("/refuseRequest")
    public ResultInfo<?> refuseRequest(Integer userId, Integer addId) {
        return friendService.refuseRequest(userId, addId);
    }

    @ApiOperation("删除好友")
    @PostMapping("/deleteFriend")
    public ResultInfo<?> deleteFriend(Integer userId, Integer friendId) {
        return friendService.deleteFriend(userId, friendId);
    }
}
