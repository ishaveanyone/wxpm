package com.dist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *创建一个工具用于添加数据项
 *
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getClassLoader().
                getResource("sample.fxml"));

        primaryStage.setTitle("客户端");

        primaryStage.setScene(new Scene(root, 1040, 575));


        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);
    }
}
