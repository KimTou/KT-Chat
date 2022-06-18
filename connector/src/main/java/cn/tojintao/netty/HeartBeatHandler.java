package cn.tojintao.netty;

import cn.tojintao.service.RedisService;
import cn.tojintao.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author cjt
 * @date 2022/6/14 23:48
 */
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt; //强制类型转化
            if (event.state() == IdleState.READER_IDLE) { //没有发起读的请求
                System.out.println("读空闲......");
            } else if (event.state() == IdleState.WRITER_IDLE) { //没有发起写的请求
                System.out.println("写空闲......");
            } else if (event.state() == IdleState.ALL_IDLE) {
                System.out.println("读写空闲......");
            }
            System.out.println("空闲检测，关闭连接");
            RedisService redisService = SpringUtil.getBean(RedisService.class);
            redisService.offline(UserChannelRelation.getUserByChannel(ctx.channel()));
            ChatHandler.userClients.remove(ctx.channel());
            UserChannelRelation.offline(ctx.channel());
            ctx.channel().close();
        }
    }
}
