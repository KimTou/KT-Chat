package cn.tojintao.model.vo;

import cn.tojintao.model.entity.User;

/**
 * @author cjt
 * @date 2021/6/21 16:25
 */
public class UserVo {

    private User user;

    /**
     * 0为离线，1为在线
     */
    private boolean status;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserVo{" +
                "user=" + user +
                ", status=" + status +
                '}';
    }
}
