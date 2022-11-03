package cn.tojintao.controller;

import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.service.AdminService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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
}
