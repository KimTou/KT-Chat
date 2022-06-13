package cn.tojintao.model.vo;

import cn.tojintao.model.entity.Group;

/**
 * @author cjt
 * @date 2021/6/21 9:46
 */
public class BoxVo {

    private UserVo userVo;

    private Group group;

    /**
     * 类型（0私聊，1群聊）
     */
    private boolean type;

    public UserVo getUserVo() {
        return userVo;
    }

    public void setUserVo(UserVo userVo) {
        this.userVo = userVo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "BoxVo{" +
                "userVo=" + userVo +
                ", group=" + group +
                ", type=" + type +
                '}';
    }
}
