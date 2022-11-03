package cn.tojintao.repository;

import cn.tojintao.repository.entity.MessageEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author cjt
 * @date 2022/10/6 16:37
 */
public interface MessageRepository extends ElasticsearchRepository<MessageEntity, Long> {

}
