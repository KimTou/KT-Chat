package cn.tojintao.snowflake.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckVO {
    private long timestamp;
    private int workID;
}
