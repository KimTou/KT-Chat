package cn.tojintao.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author cjt
 * @date 2022/11/5 10:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Ban {

    private Integer id;

    private Integer userId;

    private Integer days;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
}
