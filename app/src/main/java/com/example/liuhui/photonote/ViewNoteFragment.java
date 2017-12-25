package com.example.liuhui.photonote;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2017/12/21.
 */

public class ViewNoteFragment extends Fragment {
    public ViewNoteFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view_note, container, false);
    }
}
