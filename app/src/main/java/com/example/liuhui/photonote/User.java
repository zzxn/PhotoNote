package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;

/**
 * Created by liuhui on 18-1-4.
 */

public class User extends DataSupport {
    private String username;
    private String password;
    private long id;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.setId(this.getBaseObjId());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
}
