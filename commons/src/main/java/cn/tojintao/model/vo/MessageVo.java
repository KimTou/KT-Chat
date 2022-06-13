package cn.tojintao.model.vo;

import cn.tojintao.model.entity.Message;

/**
 * @author cjt
 * @date 2021/6/20 23:44
 */
public class MessageVo extends Message {

    private String senderName;

    private String senderAvatar;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    @Override
    public String toString() {
        return "MessageVo{" +
                "senderName='" + senderName + '\'' +
                ", senderAvatar='" + senderAvatar + '\'' +
                ", message=" + super.toString() + '\'' +
                '}';
    }
}
