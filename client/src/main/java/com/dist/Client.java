package com.dist;

import com.dist.util.CompantUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.Data;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2019/4/12.
 */
@Data
public class Client implements Runnable{

    private String address;

    private int  port;


    public void run()  {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).
                    handler(new ClientInit());
            ChannelFuture channelFuture = null;
            try {
                channelFuture = bootstrap.connect(address, port).sync();
                Channel channel=channelFuture.channel();
                CompantUtil.compantMap.put("channel",channel);
                channelFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
