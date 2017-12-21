package com.example.liuhui.photonote;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2017/12/17.
 */

public class NoteEntryAdapter extends ArrayAdapter<NoteEntry> {
    private int resource;
    private List<NoteEntry> objects;

    public NoteEntryAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<NoteEntry> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.objects = objects;
    }

    @Nullable
    @Override
    public NoteEntry getItem(int position) {
        return objects.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        NoteEntry noteEntry = this.getItem(position);

        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.image = view.findViewById(R.id.note_image);
            viewHolder.title = view.findViewById(R.id.note_title);
            viewHolder.date = view.findViewById(R.id.note_date);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.image.setImageBitmap(noteEntry.getPicture());
        viewHolder.title.setText(noteEntry.getTitle());
        viewHolder.date.setText(noteEntry.getDate().toString());
        Log.d(TAG, "getView: Successfully getItem at position " + position);

        return view;
    }

    private class ViewHolder {
        ImageView image;
        TextView title;
        TextView date;
    }
}
