package com.himalaya;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1776529715886739051L;

    private String name;
    private int age;
    private String nickName;

    public UserInfo(String name, int age, String nickName) {
        this.name = name;
        this.age = age;
        this.nickName = nickName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", nickName='" + nickName + '\'' +
                '}';
    }
}
