package cn.tojintao.exception;

import cn.tojintao.common.CodeEnum;

public class ConditionException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private int code;

    public ConditionException(int code, String name){
        super(name);
        this.code = code;
    }

    public ConditionException(String name){
        super(name);
        code = CodeEnum.BAD_REQUEST.getCode();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
