package cn.tojintao.mapper;

import cn.tojintao.model.entity.Group;
import cn.tojintao.model.entity.GroupMessage;
import cn.tojintao.model.entity.Message;
import cn.tojintao.model.vo.MessageVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author cjt
 * @date 2021/6/20 20:09
 */
@Repository
public interface ChatMapper {

    List<MessageVo> getChatById(@Param("tableNum") int tableNum, @Param("userId") Integer userId,
                                @Param("friendId") Integer friendId);

    List<GroupMessage> getGroupChatById(@Param("tableNum") int tableNum, @Param("groupId") Integer groupId);

    void saveMessage(@Param("tableNum") int tableNum, @Param("message") Message message);

    void deleteChat(@Param("tableNum") int tableNum, @Param("userId") Integer userId, @Param("friendId") Integer friendId);

    void saveGroupMessage(@Param("tableNum") int tableNum, @Param("groupMessage") GroupMessage groupMessage);

    List<Group> getAllGroup(Integer userId);

    Integer insertGroup(Group group);

    void outGroup(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    void intoGroup(@Param("userId") Integer userId, @Param("groupId") Integer groupId);

    List<Integer> getUserGroupIds(@Param("userId") Integer userId);

    Group findGroupByName(String groupName);

}
