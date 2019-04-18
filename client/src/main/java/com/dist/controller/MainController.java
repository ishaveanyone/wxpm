package com.dist.controller;


import com.dist.Client;
import com.dist.define.ResponseData;
import com.dist.util.CompantUtil;
import io.netty.channel.Channel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import lombok.Data;

/**
 * 主窗口
*/
@Data
public class MainController {

    public MainController() {
        CompantUtil.compantMap.put("mainController",this);
        System.out.println();
    }

    @FXML private Pane mainPane;

    @FXML private TextField ip;

    @FXML private TextField port;

    @FXML private TextField pid;

    @FXML private Label connLable;

    @FXML private Label killLable;
    @FXML private Label restartLable;

    @FXML private TextField seachStartedValue;

    @FXML private ChoiceBox optionValue;//下拉工具条

    @FXML private ListView listStartPro;

    @FXML private TextField configSearchValue;

    @FXML private ListView listConfigPro;









    public void connServer(ActionEvent event){
        System.out.println(ip+"---"+port);
//        地址
        String ip=this.ip.getText();
//        端口
        String port=this.port.getText();


//      启动客户端
        Client client=new Client();
        client.setAddress(ip);
        client.setPort(Integer.valueOf(port));
        new Thread(client).start();
    }

    public void seachStarted(ActionEvent event){
        String msg=seachStartedValue.getText();

        try {
            String value =optionValue.getValue().toString();
            String seachStartedValue=this.getSeachStartedValue().getText();
            if("name".equals(value)){
                Channel channel=(Channel)CompantUtil.compantMap.get("channel");
                ResponseData.Message myMessage = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ClientFindStartedProType)
                        .setClientFindStartedPro(ResponseData.ClientFindStartedPro.newBuilder().setType("name")
                                .setValue(seachStartedValue).build()
                        ).build();
                channel.writeAndFlush(myMessage);
            }else if("port".equals(value)){
                Channel channel=(Channel)CompantUtil.compantMap.get("channel");
                ResponseData.Message myMessage = ResponseData.Message.newBuilder()
                        .setDataType(ResponseData.Message.DataType.ClientFindStartedProType)
                        .setClientFindStartedPro(ResponseData.ClientFindStartedPro.newBuilder().setType("port")
                                .setValue(seachStartedValue).build()
                        ).build();
                channel.writeAndFlush(myMessage);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public void mouseType(){
        try {
            String test = (String) this.listStartPro.getFocusModel().getFocusedItem();
            if(test==null){
                return;
            }
            String tmp1 = test.split(",")[1].substring(1, test.split(",")[1].length() - 1);
            this.pid.setText(tmp1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }




    public void kill(){
        Channel channel=(Channel)CompantUtil.compantMap.get("channel");
        String value=this.pid.getText();
        ResponseData.Message myMessage = ResponseData.Message.newBuilder()
                .setDataType(ResponseData.Message.DataType.ClientKillStartedProType)
                .setClientKillStartedPro(ResponseData.ClientKillStartedPro.newBuilder().setValue(value).build()
                ).build();
        channel.writeAndFlush(myMessage);
    }



    public void configSearch(ActionEvent actionEvent){
        Channel channel=(Channel)CompantUtil.compantMap.get("channel");

        String value=this.configSearchValue.getText();

        ResponseData.Message myMessage = ResponseData.Message.newBuilder()
                .setDataType(ResponseData.Message.DataType.ClientFindConfigProType)
                .setClientFindConfigPro(ResponseData.ClientFindConfigPro.newBuilder().setValue(value).build()
                ).build();
        channel.writeAndFlush(myMessage);
    }

    public void start(ActionEvent actionEvent){
        System.out.println("start---");
        Channel channel=(Channel)CompantUtil.compantMap.get("channel");
        try {
            ObservableList items = FXCollections.observableArrayList();
            Object object= this.listConfigPro.getFocusModel();
            if(object==null){
                return;
            }
            String value=(String) this.listConfigPro.getFocusModel().getFocusedItem();
            ResponseData.Message myMessage = ResponseData.Message.newBuilder()
                    .setDataType(ResponseData.Message.DataType.ClientStartProType)
                    .setClientStartPro(ResponseData.ClientStartPro.newBuilder().
                            setValue(value).build()
                    ).build();
            System.out.println(myMessage.getDataType());
            channel.writeAndFlush(myMessage);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
