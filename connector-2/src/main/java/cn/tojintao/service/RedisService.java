package cn.tojintao.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author cjt
 * @date 2022/6/12 11:09
 */
@Component
public class RedisService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${netty.connector-url}")
    public String connectorUrl;
    @Value("${dubbo.protocol.port}")
    private String dubboPort;

    private static final String RELATION_KEY = "user:connector:relation";
    private static final String ONLINE_USER = "online_user";
    private static final String BAN_USER = "ban_user:";
    private static final String DUBBO_PORT = "dubbo_port:";

    public void online(Integer userId) {
        redisTemplate.opsForHash().put(RELATION_KEY, String.valueOf(userId), connectorUrl);
        redisTemplate.opsForSet().add(ONLINE_USER, String.valueOf(userId));
        redisTemplate.opsForValue().set(DUBBO_PORT + connectorUrl, dubboPort);
    }

    public void offline(Integer userId) {
        redisTemplate.opsForHash().delete(RELATION_KEY, String.valueOf(userId));
        redisTemplate.opsForSet().remove(ONLINE_USER, String.valueOf(userId));
        redisTemplate.delete(DUBBO_PORT + connectorUrl);
    }

    public String getConnectorUrl(Integer userId) {
        return (String) redisTemplate.opsForHash().get(RELATION_KEY, String.valueOf(userId));
    }

    public String getDubboPort(String connectorUrl) {
        return redisTemplate.opsForValue().get(DUBBO_PORT + connectorUrl);
    }

    public void nettyStop(Set<String> userIdSet) {
        for (String userId : userIdSet) {
            redisTemplate.opsForHash().delete(RELATION_KEY, userId);
            redisTemplate.opsForSet().remove(ONLINE_USER, userId);
            redisTemplate.delete(DUBBO_PORT + connectorUrl);
        }
    }

    public boolean isBan(Integer userId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BAN_USER + userId));
    }
}
