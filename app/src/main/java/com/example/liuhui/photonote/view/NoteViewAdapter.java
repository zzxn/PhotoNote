package com.example.liuhui.photonote.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.liuhui.photonote.R;

import java.util.List;

class NoteViewAdapter extends ArrayAdapter<NoteView> {

    private int resourceId;

    NoteViewAdapter(Context context, int listItemResourceId, List<NoteView> noteViews) {
        super(context, listItemResourceId, noteViews);
        resourceId = listItemResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        NoteView noteView = this.getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.notePhoto = view.findViewById(R.id.note_photo);
            viewHolder.noteNumber = view.findViewById(R.id.note_number);
            viewHolder.noteIsSelected = view.findViewById(R.id.note_is_selected);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        if (noteView != null) {
            viewHolder.notePhoto.setImageBitmap(noteView.getNotePhoto());
            viewHolder.noteNumber.setText(noteView.getNoteNumber() + "");
            viewHolder.noteIsSelected.setVisibility((noteView.isSelected()) ? View.VISIBLE : View.INVISIBLE);
        }

        return view;
    }

    private class ViewHolder {
        ImageView notePhoto;
        TextView noteNumber;
        ImageView noteIsSelected;
    }
}
