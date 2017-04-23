package com.punbook.mayankgupta.havi.dummy;

import java.io.Serializable;
import java.util.List;

/**
 * Created by mayankgupta on 13/03/17.
 */

public class User implements Serializable{

    private String username;
    private String age;
    private String token;
    private String gender;
    private String mobile;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", age='" + age + '\'' +
                ", token='" + token + '\'' +
                ", gender='" + gender + '\'' +
                ", mobile='" + mobile + '\'' +
                '}';
    }
}
