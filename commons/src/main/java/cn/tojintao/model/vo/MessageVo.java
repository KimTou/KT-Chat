package cn.tojintao.model.vo;

import cn.tojintao.model.entity.Message;

/**
 * @author cjt
 * @date 2021/6/20 23:44
 */
public class MessageVo extends Message {

    private String userName;

    private String avatar;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "MessageVo{" +
                "userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", message=" + super.toString() + '\'' +
                '}';
    }
}
