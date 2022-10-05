package cn.tojintao.netty;

import cn.tojintao.service.RedisService;
import cn.tojintao.util.SpringUtil;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author cjt
 * @date 2022/5/4 20:56
 */
public class NettyServer {
    private static final String nacosServer = "http://localhost:8848";
    private static final String nettyName = "netty-service";

    private static final int PORT = 9001;

    private static NettyServer nettyServer = new NettyServer();

    public static NettyServer getInstance() {
        return nettyServer;
    }

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    private Channel channel;

    public static void main(String[] args) {
        new NettyServer().run();
    }

    public void run() {
        final NettyServer nettyServer = new NettyServer();
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerInitializer());
            InetSocketAddress socketAddress = new InetSocketAddress(PORT);
            ChannelFuture channelFuture = serverBootstrap.bind(socketAddress).sync();
            channelFuture.syncUninterruptibly();
            channel = channelFuture.channel();
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    nettyServer.stop();
                }
            });

            //获取nacos服务
            NamingService namingService = NamingFactory.createNamingService(nacosServer);
            InetAddress address = InetAddress.getLocalHost();
            //将服务注册到注册中心
            namingService.registerInstance(nettyName, address.getHostAddress(), PORT);

            channelFuture.channel().closeFuture().syncUninterruptibly();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    /**
     * 停止即时通讯服务器
     */
    public void stop() {
        RedisService redisService = SpringUtil.getBean(RedisService.class);
        Set<String> userIdSet = UserChannelRelation.userChannel.keySet().stream()
                .map(String::valueOf).collect(Collectors.toSet());
        redisService.nettyStop(userIdSet);  //清空Redis存储该netty维持的长连接信息
        if (channel != null) {
            channel.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    class ChatServerInitializer extends ChannelInitializer<Channel> {
        private static final int READ_IDLE_TIME_OUT = 3600; // 读超时
        private static final int WRITE_IDLE_TIME_OUT = 0;// 写超时
        private static final int ALL_IDLE_TIME_OUT = 0; // 所有超时
        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            //websocket基于http协议，需要使用http编解码器
            pipeline.addLast(new HttpServerCodec());
            //http是以块方式写，添加ChunkedWriteHandler处理器
            pipeline.addLast(new ChunkedWriteHandler());
            //将HTTP消息的多个部分合成一条完整的HTTP消息
            pipeline.addLast(new HttpObjectAggregator(65535));
            // WebSocket数据压缩
            pipeline.addLast(new WebSocketServerCompressionHandler());
            //对于websocket来说，它的数据都是以frame帧形式传输的，不同的数据类型对应的frame也不同
            //WebSocket协议处理器，负责WebSocket的握手处理以及协议升级（通过状态码101）
            pipeline.addLast(new WebSocketServerProtocolHandler("/ws", null, true, 10 * 1024));
            //当连接在60秒内没有接收到消息时，就会触发一个IdleStateEvent事件，
            //此事件被HeartbeatHandler的userEventTriggered方法处理到
            pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME_OUT, WRITE_IDLE_TIME_OUT, ALL_IDLE_TIME_OUT, TimeUnit.SECONDS));
            pipeline.addLast(new HeartBeatHandler());
            //自定义Handler，处理业务逻辑
            pipeline.addLast(new ChatHandler());
        }
    }

}
