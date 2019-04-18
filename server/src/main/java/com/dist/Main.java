package com.dist;

import com.dist.controller.MainController;
import com.dist.util.CompantUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 *创建一个工具用于添加数据项
 *
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getClassLoader().
                getResource("sample.fxml"));

        primaryStage.setTitle("服务端配置");

        primaryStage.setScene(new Scene(root, 740, 575));

        Platform.runLater(new Runnable() {
            public void run() {
                System.out.println("重新刷新");
                //更新JavaFX的主线程的代码放在此处
                MainController mainController = (MainController)CompantUtil.compantMap.get("mainController");
                //开始向其中填入数据
                mainController.reflashListView();
            }
        });
        primaryStage.show();

//        启动服务端
        Thread thread =new Thread(new Server());
        thread.start();

        primaryStage.setOnCloseRequest(
                new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.out.print("监听到窗口关闭");
                try {
                    System.out.println("test");
                    thread.interrupt();
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    System.out.println("interrupt");
                    //抛出InterruptedException后中断标志被清除，标准做法是再次调用interrupt恢复中断
                    thread.interrupt();
                }
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
