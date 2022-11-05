package cn.tojintao.controller;

import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.vo.UserVo;
import cn.tojintao.service.AdminService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author cjt
 * @date 2022/11/2 22:02
 */
@RestController
@RequestMapping("/user-info/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @ApiOperation(value = "管理员登录")
    @PostMapping("/login")
    public ResultInfo<JSONObject> login(String name, String password) throws Exception {
        ResultInfo<JSONObject> resultInfo = adminService.login(name, password);
        return resultInfo;
    }

    @ApiOperation(value = "刷新token")
    @PostMapping("/refresh-tokens")
    public ResultInfo<JSONObject> refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        return adminService.refreshAccessToken(refreshToken);
    }

    @ApiOperation("获取所有用户")
    @GetMapping("/getAllUser")
    public ResultInfo<List<UserVo>> getAllUser() {
        return adminService.getAllUser();
    }

    @ApiOperation("禁言用户")
    @PostMapping("/banUser")
    public ResultInfo<String> banUser(Integer userId, String days) {
        return adminService.banUser(userId, days);
    }

    @ApiOperation("禁言解封")
    @PostMapping("/unBan")
    public ResultInfo<String> unBan(Integer userId) {
        return adminService.unBan(userId);
    }

    @GetMapping("/getAllGroup")
    public ResultInfo<List<Group>> getAllGroup() {
        return adminService.getAllGroup();
    }

    @PostMapping("/deleteGroup")
    public ResultInfo<?> deleteGroup(Integer groupId) {
        return adminService.deleteGroup(groupId);
    }
}
