/**
 * Created by dshelygin on 05.09.2017.
 * Сервер netty
 */

import handlers.MainHttpHandler;
import io.netty.bootstrap.ServerBootstrap;

import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;


import java.util.logging.Level;
import java.util.logging.Logger;

class NettyHttpServer  {
    final private static Logger logger = Logger.getLogger(NettyHttpServer.class.getName());

    private ChannelFuture channel;
    private final EventLoopGroup masterGroup;
    private final EventLoopGroup slaveGroup;



    NettyHttpServer() {
        masterGroup = new NioEventLoopGroup();
        slaveGroup = new NioEventLoopGroup();
    }

    void start() {
        //todo разобраться с хуком
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                serverShutdown();
            }
        });

        try {

            final ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(masterGroup,slaveGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler( new ChannelInitializer<SocketChannel>() {
                                        @Override
                                        public void initChannel(final SocketChannel ch)
                                                throws Exception {
                                            ch.pipeline().addLast("codec", new HttpServerCodec());
                                            ch.pipeline().addLast("aggregator",
                                                new HttpObjectAggregator(512*1024));
                                            ch.pipeline().addLast("request",
                                                    new MainHttpHandler());
                                        }
                                   }
                    )
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            channel = bootstrap.bind(8080).sync();
        } catch (final InterruptedException e) {
            logger.log(Level.FINE,e.toString());
        }

    }

    private void serverShutdown()
        {
        slaveGroup.shutdownGracefully();
        masterGroup.shutdownGracefully();

        try
        {
            channel.channel().closeFuture().sync();
        }
        catch (InterruptedException e) {
            logger.log(Level.FINE,e.toString());
        }
    }

}
