package com.dist.config;

import lombok.Data;

/**
 * Created by Administrator on 2019/4/17.
 */

public enum  ServerResponseEnum {
    start(0,"启动"),
    started(1,"started"),
    config(2,"config"),
    kill(3,"处理成功");

    private int type;
    private String value;
    private int statue;

    ServerResponseEnum(int type,String value){
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
