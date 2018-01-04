package com.example.liuhui.photonote.view;

import android.graphics.Bitmap;

class NoteView {

    private int noteNumber = -1;

    private Bitmap notePhoto;

    private boolean isSelected = false;

    private String path = "";

    NoteView(int noteNumber, Bitmap notePhoto, String path) {
        this.noteNumber = noteNumber;
        this.notePhoto = notePhoto;
        this.path = path;
    }

    int getNoteNumber() {
        return noteNumber;
    }

    public void setNoteNumber(int noteNumber) {
        this.noteNumber = noteNumber;
    }

    Bitmap getNotePhoto() {
        return notePhoto;
    }

    public void setNotePhoto(Bitmap notePhoto) {
        this.notePhoto = notePhoto;
    }

    boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
