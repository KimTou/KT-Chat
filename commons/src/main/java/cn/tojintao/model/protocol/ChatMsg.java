package cn.tojintao.model.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author cjt
 * @date 2022/5/4 22:02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMsg implements Serializable {
    private Integer senderId; //发送者id
    private Integer receiverId; //接收者(用户/群组)id
    private String message; //消息内容
    private Integer type; //类型(1:群聊, 2:私聊)
}
