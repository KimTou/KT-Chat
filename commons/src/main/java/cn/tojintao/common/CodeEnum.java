package cn.tojintao.common;

/**
 * @author cjt
 * @date 2021/4/8 17:07
 */
public enum CodeEnum {

    // 2xx
    SUCCESS(200,"OK（请求成功）"),

    // 4xx
    BAD_REQUEST(400,"BAD REQUEST（错误请求）"),
    UNAUTHORIZED(401, "UNAUTHORIZED 未认证"),
    FORBIDDEN(403,"Forbidden（禁止访问）"),
    NOT_FOUND(404,"NOT FOUND（资源未找到）"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    TOO_MANY_REQUEST(444, "请求过于频繁"),

    // 5xx
    INTERNAL_SERVER_ERROR(500,"Internal Server Error（服务器出错）"),

    // 参数错误
    NULL_PARAM(10001,"参数不能为空"),
    PARAM_PATTERN_INVALID(10002,"参数格式错误"),
    PARAM_NOT_IDEAL(10003,"未查找到信息"),
    FILE_NOT_SUPPORT(10004,"文件格式错误"),
    FILE_UPLOAD_FAIL(10005,"文件上传失败"),
    OPEN_RED_PACKET_FAIL(10006, "抢红包失败"),
    TOO_LONG(10007, "内容过长");

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应信息
     */
    private String msg;

    CodeEnum(int code, String msg){
        this.code = code;
        this.msg =msg;
    }

    CodeEnum(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
