package cn.tojintao.service;

import cn.tojintao.model.dto.ResultInfo;
import com.alibaba.fastjson.JSONObject;

/**
 * @author cjt
 * @date 2022/11/2 22:13
 */
public interface AdminService {

    ResultInfo<JSONObject> login(String name, String password) throws Exception;

    ResultInfo<JSONObject> refreshAccessToken(String refreshToken) throws Exception;
}
