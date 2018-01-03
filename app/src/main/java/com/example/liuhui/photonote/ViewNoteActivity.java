package com.example.liuhui.photonote;

import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class ViewNoteActivity extends AppCompatActivity {

    private static final String TAG = "ViewNoteActivity";

    private ArrayList<Note> notes = new ArrayList<>();
    private int curPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        notes = getIntent().getParcelableArrayListExtra("notes");


        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return notes.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView photoView = new PhotoView(container.getContext());
                photoView.setImageBitmap(BitmapFactory.decodeFile(notes.get(position).getPath()));
                photoView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        MarkPopupWindow markPopupWindow = new MarkPopupWindow(ViewNoteActivity.this, notes.get(curPos));
                        markPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
                        return false;
                    }
                });
                container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                return photoView;
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(getIntent().getIntExtra("currentIndex", 0));
    }
}
