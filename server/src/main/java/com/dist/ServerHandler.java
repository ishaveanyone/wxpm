package com.dist;

import com.dist.controller.MainController;
import com.dist.define.ResponseData;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
/**
 * Created by Administrator on 2019/4/11.
 */
public class ServerHandler extends SimpleChannelInboundHandler<ResponseData.Message> {


    //线程组合
    private static ChannelGroup channelGroup =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext,
                                ResponseData.Message msg) throws Exception {
        System.out.println(111);
// 如果读取到数据 那么就对数据进行处理
        ResponseData.Message.DataType dataType=msg.getDataType();
        System.out.println(dataType);
        if(dataType==ResponseData.Message.DataType.ClientFindStartedProType){
            Map<Integer, String> smallMap = new HashMap<Integer, String>();
            ResponseData.ClientFindStartedPro clientFindStartedPro=
                    msg.getClientFindStartedPro();
            String connent =clientFindStartedPro.getValue();

            String type =clientFindStartedPro.getType();

            System.out.println("通过"+type+"进行服务端的进程查找");

            String command =null;
            System.out.println(connent);
            if("name".equals(type)){
                command= new StringBuilder().append("cmd /c tasklist /v /fo csv | findstr /i ").append(connent).toString();
                String line = null;
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
                int index=0;
                while ((line = bufferedReader.readLine()) != null) {
//                sb.append(new StringBuilder().append(line).append("\n").toString());
//                System.out.println(line);
                    smallMap.put(index,line);
                    index++;
                }
                String jsonStr = JSON.toJSONString(smallMap);
                ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                        .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue(jsonStr)
                                .setType(1).build()
                        ).build();
                channelHandlerContext.writeAndFlush(responMessge);
            }else if("port".equals(type)){


                Set<String> list =new HashSet<>();

                command = new StringBuilder().append("cmd /c netstat -ano|findstr ").append(connent).toString();

                String line = null;
//            StringBuilder sb = new StringBuilder();
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec(command);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"GBK"));
                int index=0;
                while ((line = bufferedReader.readLine()) != null) {
                    line=line.trim();
                    String regEx = "[' ']+";

                    Pattern p = Pattern.compile(regEx);
                    Matcher m = p.matcher(line);
                    line= m.replaceAll(",").trim();
                    if(isPort(line,connent)){
                        list.add(line.split(",")[4]);
                    }
                }
                for(String str:list){
                    String command2 = new StringBuilder().append("cmd /c tasklist /v /fo csv | findstr /i ").append(str).toString();
                    String line2 = null;
                    Runtime runtime2 = Runtime.getRuntime();
                    Process process2 = runtime2.exec(command2);
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(process2.getInputStream(),"GBK"));
                    while ((line2 = bufferedReader2.readLine()) != null) {
                        smallMap.put(index,line2);
                        index++;
                    }
                }
                command=new StringBuilder().append("cmd /c netstat -ano|findstr ").append(connent).toString();
                String jsonStr = JSON.toJSONString(smallMap);
                System.out.println(jsonStr);
                ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                        .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue(jsonStr)
                                .setType(1).build()
                        ).build();
                channelHandlerContext.writeAndFlush(responMessge);
            }
        }
        else if(dataType==ResponseData.Message.DataType.ClientFindConfigProType){

            ResponseData.ClientFindConfigPro clientFindStartedPro=
                    msg.getClientFindConfigPro();
            String connent =clientFindStartedPro.getValue();

            Properties properties=MainController.properties;

            Map<String,String> map=new HashMap();
            Set keySet=properties.keySet();

            if(connent!=null&&!"".equals(connent)){
                for(Object key:keySet){
                    String keyStr=(String)key;
                    if(keyStr.contains(connent)){
                        map.put((String) key,properties.getProperty((String) key));
                    }
                }
            }else{
                for(Object key:keySet){
                    map.put((String) key,properties.getProperty((String) key));
                }
            }

            String resultStr=JSON.toJSONString(map);
            ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                    .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                    .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue(resultStr)
                            .setType(2).build()
                    ).build();
            channelHandlerContext.writeAndFlush(responMessge);
        }
        else if(dataType==ResponseData.Message.DataType.ClientStartProType){

            System.out.println("start---");
            ResponseData.ClientStartPro clientStartPro=
                    msg.getClientStartPro();
            String key =clientStartPro.getValue();


            if(!MainController.properties.containsKey(key)){
                ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                        .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue("启动失败")
                                .setType(-1).build()
                        ).build();
                channelHandlerContext.writeAndFlush(responMessge);
                return;
            }
            String exepath=MainController.properties.getProperty(key);

            System.out.println(exepath);

            String command ="cmd /c start "+ new StringBuilder().append(exepath).toString();
            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec(command);
                ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                        .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue("启动成功")
                                .setType(0).build()
                        ).build();
                channelHandlerContext.writeAndFlush(responMessge);
            }
            catch (IOException e1)
            {
                ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                        .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue("启动失败")
                                .setType(-1).build()
                        ).build();
                channelHandlerContext.writeAndFlush(responMessge);
                e1.printStackTrace();
            }
        }
        else if(dataType==ResponseData.Message.DataType.ClientKillStartedProType){
            System.out.println("杀死进程");
            ResponseData.ClientKillStartedPro clientKillStartedPro=
                    msg.getClientKillStartedPro();
            String connent = clientKillStartedPro.getValue();
            String command = new StringBuilder().append("cmd /c taskkill /pid ").
                    append(connent).append(" -t -f").toString();
            StringBuilder sb = new StringBuilder();
            Runtime runtime = Runtime.getRuntime();
            try {
                Process process = runtime.exec(command);
                ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                        .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue("处理成功")
                                .setType(3).build()
                        ).build();
            }
            catch (IOException e1) {
                ResponseData.Message responMessge = ResponseData.Message.newBuilder()
                    .setDataType(ResponseData.Message.DataType.ServerResponseDataType)
                    .setServerResponseData(ResponseData.ServerResponseData.newBuilder().setValue("处理失败")
                            .setType(-1).build()
                    ).build();
                e1.printStackTrace();
            }
        }
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        //  连接进入 那么返回连接成功的信息
        ResponseData.Message myMessage = ResponseData.Message.newBuilder()
                .setDataType(ResponseData.Message.DataType.ConnectType)
                .setConn(ResponseData.ConnectionInfo.newBuilder().setMessage("连接成功")
                        .setStatu(1).build()
                ).build();
          ctx.channel().writeAndFlush(myMessage);
          channelGroup.add(ctx.channel());

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {


    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }


    //    处理端口程序
    public  boolean isPort(String str,String vport){
        String address=str.split(",")[1];
        String port=address.substring(address.indexOf(":")+1);
        if(vport.equals(port)){
            return true;
        }else {
            return false;
        }
    }
}
