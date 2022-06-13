package cn.tojintao.common;

/**
 * @author cjt
 * @date 2022/5/4 22:16
 */
public enum ChatTypeEnum {

    GROUP(1, "群聊"),
    PERSONAL(2, "私聊");

    public final Integer type;
    public final String content;

    ChatTypeEnum(Integer type, String content){
        this.type = type;
        this.content = content;
    }

    public Integer getType() {
        return type;
    }
}
