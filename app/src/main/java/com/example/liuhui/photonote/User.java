package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2018/1/3.
 */

public class User  extends DataSupport {
    private long id;
    private String username;
    private String password;
  
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.setId(this.getBaseObjId());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
