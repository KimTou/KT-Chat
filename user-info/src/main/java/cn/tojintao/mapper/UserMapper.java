package cn.tojintao.mapper;

import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cjt
 * @date 2021/6/19 16:48
 */
@Repository
public interface UserMapper {

    User login(@Param("userName") String userName, @Param("password") String password);

    User getUserById(Integer userId);

    List<User> getUserByIdList(@Param("userIdList") List<Integer> userIdList);

    User getUserByName(String userName);

    List<Group> getAllGroup(Integer userId);

    List<Integer> getGroupUser(Integer groupId);

    Group getGroupById(Integer groupId);
}
