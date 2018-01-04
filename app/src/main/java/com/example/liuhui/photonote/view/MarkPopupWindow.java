package com.example.liuhui.photonote.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.liuhui.photonote.R;
import com.example.liuhui.photonote.model.Mark;

/**
 * Created by 16307110325 Zhu xiaoning
 * on 2018/1/2.
 */

public class MarkPopupWindow extends PopupWindow {
    private static String TAG = "MarkPopupWindow";
    private View view;
    private Button editBtn;
    private Button deleteBtn;
    private EditText editText;
    private Mark mMark;
    private Context mContext;
    private View.OnClickListener onDismiss;

    public MarkPopupWindow(Context context, Mark mark) {
        this.view = LayoutInflater.from(context).inflate(R.layout.mark_pop, null);
        this.mMark = mark;
        this.mContext = context;
        editBtn = view.findViewById(R.id.edit_btn);
        deleteBtn = view.findViewById(R.id.del_btn);
        editText = view.findViewById(R.id.edit_text);
        editText.setText(mark.getMess());
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 可编辑状态，点击进行保存
                if (editText.isEnabled()) {
                    mMark.setMess(editText.getText().toString());
                    editText.setEnabled(false);
                    editText.setVerticalScrollBarEnabled(true);
                    editBtn.setText(R.string.edit);
                    mMark.save();
                    Toast.makeText(mContext, "保存成功", Toast.LENGTH_SHORT).show();
                }
                // 不可编辑（观察）状态
                else {
                    editText.setEnabled(true);
                    editBtn.setText(R.string.save);
                }
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editText.setText("");
                mMark.delete();
                onDismiss.onClick(deleteBtn);
                dismiss();
            }
        });

        // 设置外部可点击，并当点击操作区域外面的时候，保存并退出
        this.setOutsideTouchable(true);
        this.view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int h = view.findViewById(R.id.pop_layout).getTop();
                int y = (int) motionEvent.getY();
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    if (y < h) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        
        /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(0x00000000));
        this.setAnimationStyle(R.style.pop_window_anim);
    }

    public void setOnDismiss(View.OnClickListener onDismiss) {
        this.onDismiss = onDismiss;
    }
}