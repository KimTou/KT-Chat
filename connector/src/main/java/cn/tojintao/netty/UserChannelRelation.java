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

    public static Map<ChannelHandlerContext, Integer> channelContextUser = new ConcurrentHashMap<>();

    public static void put(Integer userId, Channel channel) {
        userChannel.put(userId, channel);
    }

    public static Channel getChannel(Integer userId) {
        return userChannel.get(userId);
    }

    public static void putContext(ChannelHandlerContext context, Integer userId) {
        channelContextUser.put(context, userId);
    }

    public static Integer getUserByContext(ChannelHandlerContext context) {
        return channelContextUser.get(context);
    }

    public static void offline(ChannelHandlerContext context) {
        Integer userId = channelContextUser.get(context);
        userChannel.remove(userId);
        channelContextUser.remove(context);
    }
}
