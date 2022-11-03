package cn.tojintao.mapper;

import cn.tojintao.model.entity.Admin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author cjt
 * @date 2022/11/2 22:04
 */
@Repository
public interface AdminMapper {

    Admin login(@Param("name") String name, @Param("password") String password);

    Admin getAdminById(@Param("id") Integer id);
}
