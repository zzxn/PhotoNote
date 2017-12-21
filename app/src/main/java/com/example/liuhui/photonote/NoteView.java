package com.example.liuhui.photonote;

import android.graphics.Bitmap;

class NoteView {

    private int noteNumber = -1;

    private Bitmap notePhoto;
    
    private boolean isSelected = false;

    private String path = "";

    NoteView(int noteNumber, Bitmap notePhoto, String path){
        this.noteNumber = noteNumber;
        this.notePhoto = notePhoto;
        this.path = path;
    }

    int getNoteNumber() {
        return noteNumber;
    }

    Bitmap getNotePhoto() {
        return notePhoto;
    }

    boolean isSelected() {
        return isSelected;
    }

    public String getPath() {
        return path;
    }

    public void setNoteNumber(int noteNumber) {
        this.noteNumber = noteNumber;
    }

    public void setNotePhoto(Bitmap notePhoto) {
        this.notePhoto = notePhoto;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
