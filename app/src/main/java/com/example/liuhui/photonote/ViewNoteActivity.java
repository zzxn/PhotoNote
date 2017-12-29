package com.example.liuhui.photonote;

import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

public class ViewNoteActivity extends AppCompatActivity {

    private static final String TAG = "ViewNoteActivity";
    private ArrayList<String> paths;

    static {
        Mark.deleteAll(Mark.class);
        Mark mark = new Mark(0.5, 0.5, 1, "one：测试mark文字");
        mark.save();
        Mark mark1 = new Mark(0.3, 0.7, 1, "two：测试mark文字");
        mark1.save();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        paths = getIntent().getStringArrayListExtra("paths");
        for (int i = 0; i < paths.size(); i++) {
            Log.d(TAG, "onCreate: paths:" + paths.get(i));
        }

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return paths.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                PhotoView photoView = new PhotoView(container.getContext());
                photoView.setPos(position);
                photoView.setImageBitmap(BitmapFactory.decodeFile(paths.get(position)));
                container.addView(photoView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                return photoView;
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(pagerAdapter);
    }
}
