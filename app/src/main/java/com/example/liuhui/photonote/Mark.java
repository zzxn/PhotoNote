package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;


/**
 * Mark 用于封装note上所作的笔记
 */
public class Mark extends DataSupport {

    /* mark所处的横坐标 */
    private double x;
    /* mark所处的纵坐标 */
    private double y;
    /* mark所属的笔记的id */
    private long noteId;
    /* mark所携带的信息 */
    private String mess;

    /* 构造器 */
    public Mark(double x, double y, long noteId, String mess) {
        setX(x);
        setY(y);
        setNoteId(noteId);
        setMess(mess);
    }

    public Mark() {
    }

    /* getter and setter */

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public long getNoteId() {
        return noteId;
    }

    public void setNoteId(long noteId) {
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

}
