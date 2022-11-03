package cn.tojintao.feign.fallback;

import cn.tojintao.common.CodeEnum;
import cn.tojintao.feign.UserInfoService;
import cn.tojintao.model.dto.ResultInfo;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.User;
import cn.tojintao.model.vo.UserVo;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cjt
 * @date 2022/6/20 17:24
 */
@Component
public class UserInfoServiceFallback implements UserInfoService {
    @Override
    public ResultInfo<User> findUserById(Integer userId) {
        User user = new User();
        user.setUserId(0);
        user.setUserName("未知");
        return ResultInfo.error(CodeEnum.INTERNAL_SERVER_ERROR, user);
    }

    @Override
    public ResultInfo<Group> getGroupById(Integer groupId) {
        Group group = new Group();
        group.setGroupId(0);
        group.setGroupName("未知");
        return ResultInfo.error(CodeEnum.INTERNAL_SERVER_ERROR, group);
    }

    @Override
    public ResultInfo<List<Group>> getAllGroup(Integer userId) {
        List<Group> groupList = new ArrayList<>();
        return ResultInfo.error(CodeEnum.INTERNAL_SERVER_ERROR, groupList);
    }

    @Override
    public ResultInfo<List<UserVo>> findAllFriend(Integer userId) {
        List<UserVo> userVoList = new ArrayList<>();
        return ResultInfo.error(CodeEnum.INTERNAL_SERVER_ERROR, userVoList);
    }

    @Override
    public ResultInfo<List<Integer>> getGroupUser(Integer groupId) {
        List<Integer> groupUserList = new ArrayList<>();
        return ResultInfo.error(CodeEnum.INTERNAL_SERVER_ERROR, groupUserList);
    }
}
