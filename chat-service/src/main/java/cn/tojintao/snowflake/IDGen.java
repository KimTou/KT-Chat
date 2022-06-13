package cn.tojintao.snowflake;


import cn.tojintao.snowflake.common.Result;

public interface IDGen {
    Result get(String key);
    boolean init();
}
