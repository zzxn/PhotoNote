package com.example.liuhui.photonote;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import site.gemus.openingstartanimation.LineDrawStrategy;
import site.gemus.openingstartanimation.OpeningStartAnimation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<NoteEntry> textNoteEntryList = new ArrayList<>();
    private List<NoteEntry> pptNoteEntryList = new ArrayList<>();
    private List<NoteEntry> cardNoteEntryList = new ArrayList<>();
    private View text_tab, ppt_tab, card_tab;
    private List<View> tabViewList = new ArrayList<>();
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private NoteEntryAdapter textAdapter;
    private NoteEntryAdapter pptAdapter;
    private NoteEntryAdapter cardAdapter;
    private int currentPageIndex = 0;
    private ListView textListView;
    private ListView pptListView;
    private ListView cardListView;
    private List<Notebook> nbs;
    private ArrayList<Notebook> textNbs = new ArrayList<>();
    private ArrayList<Notebook> pptNbs = new ArrayList<>();
    private ArrayList<Notebook> cardNbs = new ArrayList<>();

    private final String TAG = "MainActivity";

    public static String newNotebookName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new OpeningStartAnimation.Builder(this).setDrawStategy(new LineDrawStrategy())
                    .setAnimationInterval(3850).setAnimationFinishTime(450).setAppStatement("Photo Note")
                .create().show(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        // set float button
        FloatingActionButton takePhoto = (FloatingActionButton) findViewById(R.id.take_photo);

        // set drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().getItem(0).setChecked(true);

        // setViewPager
        setViewPager();

        initEntry();
        textAdapter = new NoteEntryAdapter(MainActivity.this, R.layout.note_entry, textNoteEntryList);
        textListView = text_tab.findViewById(R.id.entry_list_text);
        textListView.setAdapter(textAdapter);
        pptAdapter = new NoteEntryAdapter(MainActivity.this, R.layout.note_entry, pptNoteEntryList);
        pptListView = ppt_tab.findViewById(R.id.entry_list_ppt);
        pptListView.setAdapter(pptAdapter);
        cardAdapter = new NoteEntryAdapter(MainActivity.this, R.layout.note_entry, cardNoteEntryList);
        cardListView = card_tab.findViewById(R.id.entry_list_card);
        cardListView.setAdapter(cardAdapter);

        toolbar.setTitle(R.string.text);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.new_notebook:
                        show_dialog();
                        break;
                    case R.id.action_settings:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        /* 给list view设置项目点击事件 */
        textListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notebook nb = textNbs.get(position);
                Intent intent = new Intent(MainActivity.this, ViewNotebookActivity.class);
                intent.putExtra("fromMain", true);
                intent.putExtra("type", 0);
                intent.putExtra("name", nb.getName());
                intent.putExtra("id", nb.getId());
                startActivity(intent);
            }
        });

        pptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w(TAG, "onItemClick: ppt");
            }
        });

        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w(TAG, "onItemClick: card");
            }
        });

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initEntry() {
        /* 从数据库读取notebook */
        nbs = DataSupport.findAll(Notebook.class);
        /* 好的，现在读取笔记本 */
        for (Notebook nb: nbs){
            Log.w(TAG, "initEntry: "+nb.getName() + "\\"+nb.getType() + "\\" + nb.getId());
            if (nb.getType() == Notebook.NOTE_TYPE_TEXT) {
                textNbs.add(nb);
                textNoteEntryList.add(new NoteEntry(nb.getName(), nb.getDate(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.note)));
            }
            else if (nb.getType() == Notebook.NOTE_TYPE_PPT) {
                pptNbs.add(nb);
                pptNoteEntryList.add(new NoteEntry(nb.getName(), nb.getDate(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.note)));
            }
            else {
                cardNbs.add(nb);
                cardNoteEntryList.add(new NoteEntry(nb.getName(), nb.getDate(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.note)));
            }
        }
    }

    private void setViewPager() {
        LayoutInflater inflater = getLayoutInflater();
        text_tab = inflater.inflate(R.layout.layout_tab_text, null);
        ppt_tab = inflater.inflate(R.layout.layout_tab_ppt, null);
        card_tab = inflater.inflate(R.layout.layout_tab_card, null);

        tabViewList.add(text_tab);
        tabViewList.add(ppt_tab);
        tabViewList.add(card_tab);

        PagerAdapter pagerAdapter = new PagerAdapter() {
            @Override
            public int getCount() {
                return tabViewList.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(tabViewList.get(position));
                return tabViewList.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(tabViewList.get(position));
            }
        };
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPageIndex = position;
                switch (position) {
                    case 0:
                        toolbar.setTitle(R.string.text);
                        navigationView.getMenu().getItem(0).setChecked(true);
                        break;
                    case 1:
                        toolbar.setTitle(R.string.ppt);
                        navigationView.getMenu().getItem(1).setChecked(true);
                        break;
                    case 2:
                        toolbar.setTitle(R.string.card);
                        navigationView.getMenu().getItem(2).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.text_menu:
                currentPageIndex = 0;
                viewPager.setCurrentItem(0);
                break;
            case R.id.ppt_menu:
                currentPageIndex = 1;
                viewPager.setCurrentItem(1);
                break;
            case R.id.card_menu:
                currentPageIndex = 2;
                viewPager.setCurrentItem(2);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void show_dialog(){
        final EditText inputName = new EditText(this);
//        创建对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        设置对话框的标题
        builder.setTitle("输入笔记本的名称").
                setIcon(R.drawable.notebook).
                setView(inputName).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputName.getText().toString();
                Date date = new Date();
                if (inputName.getText().toString().length() > 0) {
                        Notebook nb = new Notebook(name,date.toString(), currentPageIndex);
                        nb.save();
                        NoteEntry entry = new NoteEntry(name, date.toString(),
                                BitmapFactory.decodeResource(getResources(), R.mipmap.note));
                        switch (currentPageIndex){
                            case 0:
                                textNbs.add(nb);
                                textNoteEntryList.add(entry);
                                textAdapter.notifyDataSetChanged();
                                break;
                            case 1:
                                pptNbs.add(nb);
                                pptNoteEntryList.add(entry);
                                pptAdapter.notifyDataSetChanged();
                                break;
                            case 2:
                                cardNbs.add(nb);
                                cardNoteEntryList.add(entry);
                                cardAdapter.notifyDataSetChanged();
                                break;
                            default:
                                break;
                    }
                    Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(MainActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "取消创建", Toast.LENGTH_SHORT).show();;
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onRestart() {
//        当activity到达前台时，onRestart生命周期函数最开始被调用
        if (!newNotebookName.equals("")){
            Log.w(TAG, "new notebook name: "+newNotebookName);
            if (currentPageIndex == 0){
                Notebook nb = new Notebook(newNotebookName, (new Date()).toString(), currentPageIndex);
                nb.save();
                textNbs.add(nb);
                textNoteEntryList.add(new NoteEntry(newNotebookName,(new Date()).toString(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.note)));
                textAdapter.notifyDataSetChanged();
            }
            else if (currentPageIndex == 1){
                Notebook nb = new Notebook(newNotebookName, (new Date()).toString(), currentPageIndex);
                nb.save();
                pptNbs.add(nb);
                pptNoteEntryList.add(new NoteEntry(newNotebookName,(new Date()).toString(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.note)));
                pptAdapter.notifyDataSetChanged();
            }
            else {
                Notebook nb = new Notebook(newNotebookName, (new Date()).toString(), currentPageIndex);
                nb.save();
                cardNbs.add(nb);
                cardNoteEntryList.add(new NoteEntry(newNotebookName,(new Date()).toString(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.note)));
                cardAdapter.notifyDataSetChanged();
            }
            newNotebookName = "";
        }
        super.onRestart();
    }
}
