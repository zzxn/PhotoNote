package com.example.liuhui.photonote;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

public class Note extends DataSupport implements Parcelable {

    /* photo保存的路径 */
    private String path;

    private String mark;

    /* 笔记所属notebook的id */
    private long notebookId;

    private long id;

    /* 构造器 */
    public Note(String path, long notebookId) {
        setPath(path);
        setNotebookId(notebookId);
    }

    public Note() {
    }

    protected Note(Parcel in) {
        path = in.readString();
        notebookId = in.readLong();
        id = in.readLong();
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

    public void setNotebookId(long notebookId) {
        this.notebookId = notebookId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(notebookId);
        dest.writeLong(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            Note note = new Note();
            note.path = in.readString();
            note.notebookId = in.readLong();
            note.id = in.readLong();
            return note;
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
