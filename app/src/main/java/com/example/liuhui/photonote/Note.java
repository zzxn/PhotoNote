package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;

public class Note extends DataSupport {

    /* photo保存的路径 */
    private String path;
    /* 笔记所属notebook的id */
    private long notebookId;

    /* 构造器 */
    public Note(String path, long notebookId) {
        setPath(path);
        setNotebookId(notebookId);
    }

    public Note() {
    }

    /* getter and setter */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getNotebookId() {
        return notebookId;
    }

    public long getId() {
        return getBaseObjId();
    }

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

}
