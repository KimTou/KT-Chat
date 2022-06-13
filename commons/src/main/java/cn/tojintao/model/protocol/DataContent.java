package cn.tojintao.model.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author cjt
 * @date 2022/5/4 22:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataContent implements Serializable {
    private Integer action; //动作类型
    private ChatMsg chatMsg; //聊天内容
    private String extend; //扩展字段
}
