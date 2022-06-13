package cn.tojintao.model.dto;

import cn.tojintao.common.CodeEnum;

/**
 * @author cjt
 * @date 2021/6/19 16:30
 */
public class ResultInfo<T> {

    /**
     * 响应状态码
     */
    private int code;

    /**
     *响应状态信息
     */
    private String msg;

    /**
     *响应返回数据
     */
    private T data;

    /**
     * 成功响应
     * @param codeEnum
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResultInfo<T> success (CodeEnum codeEnum, T data){
        return new ResultInfo<T>(codeEnum, data);
    }

    public static <T> ResultInfo<T> success (Integer code, String msg, T data){
        return new ResultInfo<T>(code, msg, data);
    }

    public static <T> ResultInfo<T> success (CodeEnum codeEnum){
        return new ResultInfo<T>(codeEnum);
    }

    /**
     * 错误响应
     * @param codeEnum
     * @param <T>
     * @return
     */
    public static <T> ResultInfo<T> error (CodeEnum codeEnum){
        return new ResultInfo<>(codeEnum);
    }

    public static <T> ResultInfo<T> error (CodeEnum codeEnum, T data){
        return new ResultInfo<>(codeEnum, data);
    }

    public static <T> ResultInfo<T> error (Integer code, String msg){
        return new ResultInfo<>(code,msg);
    }

    public ResultInfo (CodeEnum codeEnum){
        this.code = codeEnum.getCode();
        this.msg =codeEnum.getMsg();
    }

    public ResultInfo (CodeEnum codeEnum, T data){
        this(codeEnum);
        this.data = data;
    }

    public ResultInfo (Integer code, String msg){
        this.code = code;
        this.msg =msg;
    }

    public ResultInfo (Integer code, String msg, T data){
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultInfo (){ }

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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultInfo{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

}
