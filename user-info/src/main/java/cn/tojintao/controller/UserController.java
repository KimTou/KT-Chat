package cn.tojintao.controller;

import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.User;
import cn.tojintao.service.UserService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author cjt
 * @date 2022/6/6 23:31
 */
@RestController
@RequestMapping("/user-info/user")
public class UserController {
    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public ResultInfo<JSONObject> login(String userName, String password) throws Exception {
        return userService.login(userName, password);
    }

    @ApiOperation(value = "刷新token")
    @PostMapping("/refresh-tokens")
    public ResultInfo<JSONObject> refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        return userService.refreshAccessToken(refreshToken);
    }

    @ApiOperation(value = "根据用户id查找用户")
    @GetMapping("/findUserById")
    public ResultInfo<User> findUserById(Integer userId) {
        return userService.findUserById(userId);
    }

    @GetMapping("/getAllGroup")
    public ResultInfo<List<Group>> getAllGroup(Integer userId) {
        return userService.getAllGroup(userId);
    }

    @GetMapping("/getGroupUser")
    public ResultInfo<List<Integer>> getGroupUser(Integer groupId) {
        return userService.getGroupUser(groupId);
    }

    @GetMapping("/getGroupById")
    public ResultInfo<Group> getGroupById(Integer groupId) {
        return userService.getGroupById(groupId);
    }

    @GetMapping("/isBan")
    public ResultInfo<String> isBan(Integer userId) {
        return userService.isBan(userId);
    }
}
