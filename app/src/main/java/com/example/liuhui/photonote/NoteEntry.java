package com.example.liuhui.photonote;

import android.graphics.Bitmap;
import java.util.Date;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2017/12/17.
 */

public class NoteEntry {
    private String title;
    private Date date;
    private Bitmap picture;

    public NoteEntry(String title, Date date, Bitmap picture) {
        this.title = title;
        this.date = date;
        this.picture = picture;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }
}
