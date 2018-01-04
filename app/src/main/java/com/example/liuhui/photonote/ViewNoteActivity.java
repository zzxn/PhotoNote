package com.example.liuhui.photonote;

import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.chrisbanes.photoview.OnLongPressListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.List;

public class ViewNoteActivity extends AppCompatActivity {

    private static final String TAG = "ViewNoteActivity";

    private ArrayList<Note> notes = new ArrayList<>();
    private ArrayList<View> containers = new ArrayList<>();
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);
        notes = getIntent().getParcelableArrayListExtra("notes");
        LayoutInflater inflater = getLayoutInflater();
        for (int i = 0; i < notes.size(); i++) {
            final Note note = notes.get(i);
            // get container
            final View container = inflater.inflate(R.layout.photo_view_container, null);

            // set photoview
            final PhotoView photoView = container.findViewById(R.id.photo_view);
            photoView.setImageBitmap(BitmapFactory.decodeFile(note.getPath()));
            photoView.setOnLongPressListener(new OnLongPressListener() {
                @Override
                public void onLongPress(float x, float y) {
                    RectF rectF = photoView.getDisplayRect();
                    float ox = rectF.centerX() - rectF.width()/2;
                    float oy = rectF.centerY() - rectF.height()/2;
                    float absX = ox + rectF.width() * x;
                    float absY = oy + rectF.height() * y;
                    final Mark newMark = new Mark(x, y, note.getId(), "");
                    newMark.save();
                    FrameLayout mark_layer = container.findViewById(R.id.mark_layer);
                    final MarkView markView = new MarkView(mark_layer.getContext(), mark_layer, newMark);

                    markView.setX(absX);
                    markView.setY(absY);
                    mark_layer.addView(markView);
//                     监控图片位置变化，使得Mark位置随之改变
                    new Thread(new Runnable() {
                        private float oldX;
                        private float oldY;
                        @Override
                        public void run() {
                            while (true) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // todo 优化多线程，避免不必要的界面更新
                                        RectF rectF = photoView.getDisplayRect();
                                        float ox = rectF.centerX() - rectF.width() / 2;
                                        float oy = rectF.centerY() - rectF.height() / 2;
                                        float x = ox + rectF.width() * newMark.getX();
                                        float y = oy + rectF.height() * newMark.getY();
                                        if (oldX != x || oldY != y) {
                                            oldX = x;
                                            oldY = y;
                                            markView.setTranslationX(x);
                                            markView.setTranslationY(y);
                                        }
                                    }
                                });
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            });

            // set mark
            FrameLayout mark_layer = container.findViewById(R.id.mark_layer);
            List<Mark> marks = Mark.where("noteid = ?", Long.toString(note.getId())).find(Mark.class);
            RectF rectF = photoView.getDisplayRect();
            for (final Mark mark : marks) {
                final MarkView markView = new MarkView(mark_layer.getContext(), mark_layer, mark);
                float ox = rectF.centerX() - rectF.width()/2;
                float oy = rectF.centerY() - rectF.height()/2;
                float x = ox + rectF.width() * mark.getX();
                float y = oy + rectF.height() * mark.getY();
                markView.setX(x);
                markView.setY(y);
                mark_layer.addView(markView);
                // 监控图片位置变化，使得Mark位置随之改变
                new Thread(new Runnable() {
                    private float oldX;
                    private float oldY;
                    @Override
                    public void run() {
                        while (true) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    // todo 优化多线程，避免不必要的界面更新
                                    RectF rectF = photoView.getDisplayRect();
                                    float ox = rectF.centerX() - rectF.width()/2;
                                    float oy = rectF.centerY() - rectF.height()/2;
                                    float x = ox + rectF.width() * mark.getX();
                                    float y = oy + rectF.height() * mark.getY();
                                    if (oldX != x || oldY != y) {
                                        oldX = x;
                                        oldY = y;
                                        markView.setTranslationX(x);
                                        markView.setTranslationY(y);
                                    }
                                }
                            });
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }

            containers.add(container);
        }


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
                container.addView(containers.get(position),
                        ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
                return containers.get(position);
            }
            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView((View) object);
            }
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(getIntent().getIntExtra("currentIndex", 0));
    }
}