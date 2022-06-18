package cn.tojintao.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author cjt
 * @date 2022/5/4 22:48
 */
public class UserChannelRelation {

    /**
     * 存储:用户->通道
     */
    public static Map<Integer, Channel> userChannel = new ConcurrentHashMap<>();

    public static Map<Channel, Integer> channelUser = new ConcurrentHashMap<>();

    public static void put(Integer userId, Channel channel) {
        userChannel.put(userId, channel);
    }

    public static Channel getChannel(Integer userId) {
        return userChannel.get(userId);
    }

    public static void put(Channel channel, Integer userId) {
        channelUser.put(channel, userId);
    }

    public static Integer getUserByChannel(Channel channel) {
        return channelUser.get(channel);
    }

    public static void offline(Channel channel) {
        Integer userId = channelUser.get(channel);
        if (userId != null) {
            userChannel.remove(userId);
            System.out.println("用户" + userId + "下线");
        }
        channelUser.remove(channel);
    }
}
