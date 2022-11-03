package cn.tojintao.repository.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author cjt
 * @date 2022/10/6 16:54
 */
@Data
@Document(indexName = "messages")
public class MessageEntity {

    @Id
    private Long id;

    @Field(type = FieldType.Integer)
    private Integer sender;

    @Field(type = FieldType.Text)
    private String userName;

    @Field(type = FieldType.Text)
    private String avatar;

    @Field(type = FieldType.Integer)
    private Integer groupId;

    @Field(type = FieldType.Text)
    private String groupName;

    @Field(type = FieldType.Integer)
    private Integer receiver;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Date)
    private Date gmtCreate;
}
