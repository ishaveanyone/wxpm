package com.dist.controller;

import com.dist.util.CompantUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.Data;

import java.io.*;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Administrator on 2019/4/18.
 */
@Data
public class MainController {

    @FXML private TextField shortName;
    @FXML private TextField path;
    @FXML private ListView configed;
    @FXML private Label pathLabel;
    public static Properties properties=new Properties();

    public MainController(){

        CompantUtil.compantMap.put("mainController",this);
    }
    static {
        try {
            /*String classpath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
            System.out.println(classpath);*/
            File file=new File("c:/config.properties");
            if(!file.exists()){
                file.createNewFile();
            }

            FileInputStream fileInputStream=new FileInputStream( "c:/config.properties");
            properties.load(fileInputStream);
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }
    public void mouseType() {
        String key = (String) this.configed.getFocusModel().getFocusedItem();
        if(key==null){
            return;
        }
        String path=properties.getProperty(key);
        this.pathLabel.setText("路径：  "+path);


    }

    public void save(ActionEvent event){
        System.out.println(111);
        String shortName=this.shortName.getText();
        String path=this.path.getText();
        if(shortName==null||"".equals(shortName)){
            return;
        }
        if(path==null||"".equals(path)){
            return;
        }
        if(properties.containsKey(shortName)){
            return;
        }
        properties.put(shortName,path);
        reflashListView();
    }

    public void delete(ActionEvent event){
        String key = (String)  configed.getFocusModel().getFocusedItem();
        if(!properties.containsKey(key)){
            return;
        }
        properties.remove(key);

        reflashListView();

    }

    public void reflashListView(){
        //开始向其中填入数据
        ObservableList items = FXCollections.observableArrayList();

        Set<Object> keys=properties.keySet();

        Iterator iterator=keys.iterator();
        while (iterator.hasNext()){
            String key=(String) iterator.next();
            System.out.println(key);
            String value=properties.getProperty(key);
            items.add(key);
        }
        this.configed.setItems(items);

        String classpath=Thread.currentThread().getContextClassLoader().getResource("").getPath();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream("c:/config.properties");
            properties.store(fos,"重新存入");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}