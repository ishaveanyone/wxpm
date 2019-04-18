package com.dist;

import com.alibaba.fastjson.JSONObject;
import com.dist.config.ServerResponseEnum;
import com.dist.controller.MainController;
import com.dist.define.ResponseData;
import com.dist.util.CompantUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Map;

/**
 * Created by Administrator on 2019/4/12.
 */
public class ClientHandler extends SimpleChannelInboundHandler<ResponseData.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx,
                                ResponseData.Message msg)
            throws Exception {
        // 如果读取到数据 那么就对数据进行处理
        final MainController mainController=(MainController) CompantUtil.compantMap.get("mainController");
        ResponseData.Message.DataType dataType=msg.getDataType();
        if(dataType==ResponseData.Message.DataType.ConnectType){
            ResponseData.ConnectionInfo connectionInfo=msg.getConn();
            if(connectionInfo.getStatu()==1){
//                说明建立成功
                Platform.runLater(new Runnable() {
                    public void run() {
                        //更新JavaFX的主线程的代码放在此处
                        mainController.getConnLable().setText("连接成功");
                    }
                });
            }else
            {
                mainController.getConnLable().setText("连接失败");
            }
        }else if(dataType== ResponseData.Message.DataType.ServerResponseDataType){
            ResponseData.ServerResponseData serverResponseData=msg.getServerResponseData();
            final String result=serverResponseData.getValue();
            int responseType = serverResponseData.getType();

            if(ServerResponseEnum.started.getType()==responseType){
                System.out.println("获取数据"+result);
                Platform.runLater(new Runnable() {
                    public void run() {
                        ObservableList items = FXCollections.observableArrayList();

                        JSONObject jsonObject = JSONObject.parseObject(result);
                        //json对象转Map
                        Map<String,Object> map = (Map<String,Object>)jsonObject;
                        for(Object str: map.values()){
                            items.add((String)str);
                        }
                        mainController.getListStartPro().setItems(null);
                        mainController.getListStartPro().setItems(items);
                    }
                });
            }else if(ServerResponseEnum.kill.getType()==responseType){
                int type=serverResponseData.getType();
                if(type==1){
//                说明删除成功
                    Platform.runLater(new Runnable() {
                        public void run() {
                            //更新JavaFX的主线程的代码放在此处
                            mainController.getKillLable().setText("删除成功");
                        }
                    });
                }else
                {
                    mainController.getKillLable().setText("连接失败");
                }
            }else if(ServerResponseEnum.config.getType()==responseType){
                Platform.runLater(new Runnable() {
                    public void run() {
                        ObservableList items = FXCollections.observableArrayList();

                        JSONObject jsonObject = JSONObject.parseObject(result);
                        //json对象转Map
                        Map<String,Object> map = (Map<String,Object>)jsonObject;
                        for(String key:map.keySet()){
                            items.add(key);
                        }
                        mainController.getListConfigPro().setItems(null);
                        mainController.getListConfigPro().setItems(items);
                    }
                });
            }
            else if(ServerResponseEnum.start.getType()==responseType){
                int type=serverResponseData.getType();
                if(type==0){
//                说明启动成功那个
                    Platform.runLater(new Runnable() {
                        public void run() {
                            //更新JavaFX的主线程的代码放在此处
                            mainController.getRestartLable().setText("启动成功");
                        }
                    });
                }else
                {
                    mainController.getRestartLable().setText("启动失败");
                }
            }
        }
    }



    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {


    }
}
