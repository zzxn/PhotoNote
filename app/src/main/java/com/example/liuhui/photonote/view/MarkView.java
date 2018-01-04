package com.example.liuhui.photonote.view;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.liuhui.photonote.R;
import com.example.liuhui.photonote.model.Mark;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2017/12/31.
 */


public class MarkView extends android.support.v7.widget.AppCompatImageView {
    private Mark mark;

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
                // 点击时变大
                MarkView.this.setScaleX(2);
                MarkView.this.setScaleY(2);
                MarkPopupWindow popupWindow = new MarkPopupWindow(context, getMark());
                popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        // dismiss时变小
                        MarkView.this.setScaleX(1);
                        MarkView.this.setScaleY(1);
                    }
                });
                popupWindow.setOnDismiss(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ViewGroup) parent).removeView(MarkView.this);
                    }
                });
            }
        });
    }

    public Mark getMark() {
        return mark;
    }

    public void setMark(Mark mark) {
        this.mark = mark;
    }
}