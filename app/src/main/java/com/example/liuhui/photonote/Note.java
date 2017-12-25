package com.example.liuhui.photonote;

import org.litepal.crud.DataSupport;

public class Note extends DataSupport {

    /* photo保存的路径 */
    private String path;

    /* note在一个notebook中的索引 */
    private int index;

    /* 笔记所属notebook的id */
    private long notebookId;

    /* note的id */
    private long id;

    /* 构造器 */
    public Note(String path, int index, long notebookId) {
        this.path = path;
        this.index = index;
        this.notebookId = notebookId;
    }

    /* getter and setter */
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getNotebookId() {
        return notebookId;
    }

    public long getId() {
        return getBaseObjId();
    }

    public void setNotebookId(int notebookId) {
        this.notebookId = notebookId;
    }

    @Override
    public void setToDefault(String fieldName) {
        super.setToDefault(fieldName);
    }
}
