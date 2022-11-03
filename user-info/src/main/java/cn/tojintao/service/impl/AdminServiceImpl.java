package cn.tojintao.service.impl;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.exception.ConditionException;
import cn.tojintao.mapper.AdminMapper;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Admin;
import cn.tojintao.service.AdminService;
import cn.tojintao.util.TokenUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author cjt
 * @date 2022/11/2 22:13
 */
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public ResultInfo<JSONObject> login(String name, String password) throws Exception {
        if (name.length() == 0 || password.length() == 0){
            return ResultInfo.error(400, "请填写完整信息");
        }
        Admin admin = adminMapper.login(name, password);
        if (admin == null){
            return ResultInfo.error(400, "用户名或密码错误");
        }
        Integer id = admin.getId();
        String accessToken = TokenUtil.generateAccessToken(id);
        String refreshToken = TokenUtil.generateRefreshToken(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", refreshToken);
        jsonObject.put("admin", admin);
        return ResultInfo.success(CodeEnum.SUCCESS, jsonObject);
    }

    @Override
    public ResultInfo<JSONObject> refreshAccessToken(String refreshToken) throws Exception {
        Integer id = TokenUtil.verifyToken(refreshToken);
        Admin admin = adminMapper.getAdminById(id);
        if (admin == null) {
            throw new ConditionException("认证token失败");
        }
        //更新双token
        String accessToken = TokenUtil.generateAccessToken(id);
        String newRefreshToken = TokenUtil.generateRefreshToken(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", accessToken);
        jsonObject.put("refreshToken", newRefreshToken);
        return ResultInfo.success(CodeEnum.SUCCESS, jsonObject);
    }
}
