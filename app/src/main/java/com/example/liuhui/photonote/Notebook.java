package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;

public class Notebook extends DataSupport{

    /* 笔记本的名称 */
    private String name;

    /* 笔记本拍摄的日期 */
    private String date;

    /* 笔记本的类型 text ppt card */
    private int type;

    /* 基本本所属dir的id */
    private long dirId;

    /* 笔记本第一条笔记的id */
    private long noteId;

    /* notebook的id */
    private long id;

    /* 构造器 */
    public Notebook(String name, String date, int type){
        this.name = name;
        this.date = date;
        this.type = type;
    }

    public Notebook(String name, String date, int type,long dirId, long noteId) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.dirId = dirId;
        this.noteId = noteId;
    }

    /* getter anf setter */
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

    public long getDirId() {
        return dirId;
    }

    public void setDirId(int dirId) {
        this.dirId = dirId;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public long getId() {
        return getBaseObjId();
    }

    @Override
    public void setToDefault(String fieldName) {
        super.setToDefault(fieldName);
    }
}
