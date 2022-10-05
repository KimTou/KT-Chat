package cn.tojintao.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author cjt
 * @date 2021/6/21 10:25
 */
public class GroupMessage implements Serializable {

    private Long id;

    private Integer groupId;

    private Integer sender;

    private String userName;

    private String avatar;

    private String content;

    private List<Integer> userIdList;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getSender() {
        return sender;
    }

    public void setSender(Integer sender) {
        this.sender = sender;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String senderAvatar) {
        this.avatar = senderAvatar;
    }

    public List<Integer> getUserIdList() {
        return userIdList;
    }

    public void setUserIdList(List<Integer> userIdList) {
        this.userIdList = userIdList;
    }

    @Override
    public String toString() {
        return "GroupMessage{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", sender=" + sender +
                ", userName='" + userName + '\'' +
                ", avatar='" + avatar + '\'' +
                ", content='" + content + '\'' +
                ", userIdList=" + userIdList +
                ", gmtCreate=" + gmtCreate +
                '}';
    }
}
