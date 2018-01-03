package com.example.liuhui.photonote;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2017/12/31.
 */

public class MarkView extends android.support.v7.widget.AppCompatImageView  {
    private Mark mark;

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }

    public MarkView(final Context context, final View parent, final Mark mark) {
        super(context);
        setMark(mark);
        this.setImageResource(R.drawable.ic_place);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(64, 64);
        this.setLayoutParams(layoutParams);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mark == null) {
                    Toast.makeText(getContext(), "Mark未初始化", Toast.LENGTH_SHORT).show();
                        return;
                }
                MarkPopupWindow popupWindow = new MarkPopupWindow(context, getMark());
                popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
            }
        });
    }
}
