package cn.tojintao.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cjt
 * @date 2021/6/21 0:15
 */
@Repository
public interface FriendMapper {

    List<Integer> findAllFriendId(Integer userId);

    void beFriend(@Param("userId") Integer userId, @Param("addId") Integer addId);

    void insertRequest(@Param("userId") Integer userId, @Param("addId") Integer addId);

    List<Integer> getRequest(Integer userId);

    Integer requestIsExist(@Param("userId") Integer userId, @Param("addId") Integer addId);

    Integer isFriend(@Param("userId") Integer userId, @Param("addId") Integer addId);

    void deleteRequest(@Param("userId") Integer userId, @Param("addId") Integer addId);

    void deleteFriend(@Param("userId") Integer userId, @Param("friendId") Integer friendId);

}
