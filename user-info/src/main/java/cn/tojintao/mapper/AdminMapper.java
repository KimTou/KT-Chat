package cn.tojintao.mapper;

import cn.tojintao.model.entity.Admin;
import cn.tojintao.model.entity.Ban;
import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.User;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cjt
 * @date 2022/11/2 22:04
 */
@Repository
public interface AdminMapper {

    Admin login(@Param("name") String name, @Param("password") String password);

    Admin getAdminById(@Param("id") Integer id);

    List<User> getAllUser();

    void insertBan(@Param("ban") Ban ban);

    List<Group> getAllGroup();

    void deleteGroup(@Param("groupId") Integer groupId);

    void deleteGroupUser(@Param("groupId") Integer groupId);
}
