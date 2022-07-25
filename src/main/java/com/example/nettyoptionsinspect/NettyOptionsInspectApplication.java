package com.example.nettyoptionsinspect;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;

import java.util.Map;

@SpringBootApplication
public class NettyOptionsInspectApplication implements CommandLineRunner {

    @Value("${server.port}")
    private int port;

    public static void main(String[] args) {
        final SpringApplication app = new SpringApplicationBuilder(NettyOptionsInspectApplication.class).build();
        app.addListeners(new ApplicationPidFileWriter("./app.pid"));
        app.run(args);
    }

    @Autowired
    private NettyProps props;

    private void initOptions(ServerBootstrap bootstrap) {
        if (props.getTcpNodelay() != null) {
            bootstrap.childOption(ChannelOption.TCP_NODELAY, props.getTcpNodelay());
        }
        if (props.getTcpFastopenConnect() != null) {
            bootstrap.childOption(ChannelOption.TCP_FASTOPEN_CONNECT, props.getTcpFastopenConnect());
        }
        if (props.getSoBacklog() != null) {
            bootstrap.childOption(ChannelOption.SO_BACKLOG, props.getSoBacklog());
        }
        if (props.getSoKeepalive() != null) {
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, props.getSoKeepalive());
        }
        if (props.getSoRcvbuf() != null) {
            bootstrap.childOption(ChannelOption.SO_RCVBUF, props.getSoRcvbuf());
        }
        if (props.getSoReuseaddr() != null) {
            bootstrap.childOption(ChannelOption.SO_REUSEADDR, props.getSoReuseaddr());
        }
        if (props.getSoSndbuf() != null) {
            bootstrap.childOption(ChannelOption.SO_SNDBUF, props.getSoSndbuf());
        }
        if (props.getSoBroadcast() != null) {
            bootstrap.childOption(ChannelOption.SO_BROADCAST, props.getSoBroadcast());
        }
        if (props.getSoLinger() != null) {
            bootstrap.childOption(ChannelOption.SO_LINGER, props.getSoLinger());
        }
        if (props.getSoTimeout() != null) {
            bootstrap.childOption(ChannelOption.SO_TIMEOUT, props.getSoTimeout());
        }
        if (props.getMaxMessagesPerWrite() != null) {
            bootstrap.childOption(ChannelOption.MAX_MESSAGES_PER_WRITE, props.getMaxMessagesPerWrite());
        }
        if (props.getWaterlow() != null && props.getWaterhigh() != null) {
            bootstrap.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new
                    WriteBufferWaterMark(props.getWaterlow(), props.getWaterhigh()));
        }

    }

    public static final NioEventLoopGroup BOSS_GROUP = new NioEventLoopGroup(1, new DefaultThreadFactory("netty-boss"));
    public static final NioEventLoopGroup WORK_GROUP = new NioEventLoopGroup(4, new DefaultThreadFactory("netty-worker"));

    @Override
    public void run(final String... args) {
        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        try {
            initOptions(serverBootstrap);
            serverBootstrap
                    .group(BOSS_GROUP, WORK_GROUP)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    Map<ChannelOption<?>, Object> options = ctx.channel().config().getOptions();
                                    for (Map.Entry<ChannelOption<?>, Object> entry : options.entrySet()) {
                                        System.out.println("选项 " + entry.getKey() + " => " + entry.getValue());
                                    }
                                    super.channelActive(ctx);
                                }
                            });
                        }
                    });
            final Channel ch = serverBootstrap.bind(port).sync().channel();
            System.out.println("start app ok..." + port);
            ch.closeFuture().sync();
        } catch (final InterruptedException e) {
            //ignore
        } finally {
            BOSS_GROUP.shutdownGracefully();
            WORK_GROUP.shutdownGracefully();
            System.out.println("stop app ok...");
        }
    }

}
