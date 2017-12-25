package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;


/**
 * Mark 用于封装note上所作的笔记
 */
public class Mark extends DataSupport {

    /* mark所处的横坐标 */
    private float x;

    /* mark所处的纵坐标 */
    private float y;

    /* mark所属的笔记的id */
    private long noteId;

    /* mark的id */
    private long id;

    /* mark所携带的信息 */
    private String mess;

    /* 构造器 */
    public Mark(float x, float y, long noteId, String mess) {
        this.x = x;
        this.y = y;
        this.noteId = noteId;
        this.mess = mess;
    }

    /* getter and setter */

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getMess() {
        return mess;
    }

    public void setMess(String mess) {
        this.mess = mess;
    }

    public long getId() {
        return getBaseObjId();
    }

    @Override
    public void setToDefault(String fieldName) {
        super.setToDefault(fieldName);
    }
}
