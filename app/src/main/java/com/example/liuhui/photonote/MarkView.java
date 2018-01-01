package com.example.liuhui.photonote;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2017/12/31.
 */

public class MarkView extends ImageView implements View.OnClickListener {
    private Mark mark;

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    @Override
    public void onClick(View view) {
        Mark curMark = getMark();
        if (curMark == null) {
            Toast.makeText(getContext(), "Mark未初始化", Toast.LENGTH_SHORT).show();
            return;
        }
        // todo set click listener
        return;
    }

    public MarkView(Context context) {
        super(context);
    }

    public MarkView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarkView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


}
