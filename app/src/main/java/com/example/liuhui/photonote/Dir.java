package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;

public class Dir extends DataSupport {

    /* 文件夹名称 */
    private String name;

    /* 文件夹创建日期 */
    private String date;

    /* 文件夹类型 */
    private int type;

    /* dir的id */
    private long id;

    /* 构造器 */
    public Dir(String name, String date, int type) {
        this.name = name;
        this.date = date;
        this.type = type;
    }

    /* getter and setter */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getId() {
        return getBaseObjId();
    }

    @Override
    public void setToDefault(String fieldName) {
        super.setToDefault(fieldName);
    }
}
