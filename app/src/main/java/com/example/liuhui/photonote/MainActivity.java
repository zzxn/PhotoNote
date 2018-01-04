package com.example.liuhui.photonote;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import site.gemus.openingstartanimation.OpeningStartAnimation;
import site.gemus.openingstartanimation.RedYellowBlueDrawStrategy;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private View text_tab, ppt_tab, card_tab;
    private List<View> tabViewList = new ArrayList<>();
    private Toolbar toolbar;
    private NavigationView navigationView;
    private ViewPager viewPager;
    private FloatingActionButton deleteNotebook;
    /* 三个adapter */
    private NoteEntryAdapter textAdapter;
    private NoteEntryAdapter pptAdapter;
    private NoteEntryAdapter cardAdapter;

    /* 当前页面的索引 */
    private int currentPageIndex = 0;

    /* 与adapter相对应的ListView */
    private ListView textListView;
    private ListView pptListView;
    private ListView cardListView;

    /* 所有从数据库中读出来的Notebook */
    private List<Notebook> nbs;

    /* 三种不同type的NoteEntry */
    private List<NoteEntry> textNoteEntryList = new ArrayList<>();
    private List<NoteEntry> pptNoteEntryList = new ArrayList<>();
    private List<NoteEntry> cardNoteEntryList = new ArrayList<>();

    /* 三种不同type的notebook */
    private ArrayList<Notebook> textNbs = new ArrayList<>();
    private ArrayList<Notebook> pptNbs = new ArrayList<>();
    private ArrayList<Notebook> cardNbs = new ArrayList<>();

    /* 三种不同type的notebook被选择的个数 */
    private int textSelectedNumber = 0;
    private int pptSelectedNumber = 0;
    private int cardSelectedNumber = 0;

    /* 存储当前user的id */
    private long currentUserId = 0;

    private final String TAG = "MainActivity";

    private ImageView headPortrait;
    private SharedPreferences.Editor write;
    private String path;
    private File saveFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean directStart = getIntent().getBooleanExtra("directStart", false);
        if(directStart)
            new OpeningStartAnimation.Builder(this).setDrawStategy(new RedYellowBlueDrawStrategy())
                    .setAnimationInterval(3850).setAnimationFinishTime(450).setAppStatement("Photo Note")
                    .create().show(this);

        currentUserId = getIntent().getLongExtra("currentUserId", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        saveFile = new File(getExternalFilesDir("img"), "head.jpg");
        path = saveFile.getAbsolutePath();

        // set two floating buttons
        FloatingActionButton takePhoto = (FloatingActionButton) findViewById(R.id.take_photo);
        deleteNotebook = (FloatingActionButton) findViewById(R.id.delete_notebook);

        // set drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        headPortrait = navigationView.getHeaderView(0).findViewById(R.id.head_portrait);
        navigationView.setNavigationItemSelectedListener(this);

        List<User> user = DataSupport.where("id = ?", currentUserId+"").find(User.class);
        navigationView.getMenu().getItem(0).setChecked(true);
        /* 设置用户名 */
        navigationView.getMenu().getItem(4).setTitle(user.get(0).getUsername());

        // setViewPager
        setViewPager();

        /* 初始化操作 */
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

        /* 菜单项响应点击事件
         * 只有创建新的笔记本这个菜单设置了相应的逻辑
          * */
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.new_notebook:
                        show_dialog();
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////
        /* 给text list view设置项目点击事件
         * 点击某个笔记本之后，会跳转到ViewNotebookActivity
          * */
        textListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notebook nb = textNbs.get(position);
                Intent intent = new Intent(MainActivity.this, ViewNotebookActivity.class);
                /* 凡是由MainActivity启动的ViewNotebookActivity
                 * 这个笔记本便是已经创建好的笔记本
                  * */
                intent.putExtra("fromMain", true);
                /* 传入已经创建好的笔记本的名字和id */
                intent.putExtra("name", nb.getName());
                intent.putExtra("id", nb.getId());
                startActivity(intent);
            }
        });

        /* 给ppt list view设置项目点击事件
         * 点击某个笔记本之后，会跳转到ViewNotebookActivity
          * */
        pptListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notebook nb = pptNbs.get(position);
                Intent intent = new Intent(MainActivity.this, ViewNotebookActivity.class);
                /* 凡是由MainActivity启动的ViewNotebookActivity
                 * 这个笔记本便是已经创建好的笔记本
                  * */
                intent.putExtra("fromMain", true);
                /* 传入已经创建好的笔记本的名字和id */
                intent.putExtra("name", nb.getName());
                intent.putExtra("id", nb.getId());
                startActivity(intent);
            }
        });

        /* 给card list view设置项目点击事件
         * 点击某个笔记本之后，会跳转到ViewNotebookActivity
          * */
        cardListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Notebook nb = cardNbs.get(position);
                Intent intent = new Intent(MainActivity.this, ViewNotebookActivity.class);
                /* 凡是由MainActivity启动的ViewNotebookActivity
                 * 这个笔记本便是已经创建好的笔记本
                  * */
                intent.putExtra("fromMain", true);
                /* 传入已经创建好的笔记本的名字和id */
                intent.putExtra("name", nb.getName());
                intent.putExtra("id", nb.getId());
                startActivity(intent);
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////////////////////////////////////////////////////////////
        /* 删除notebook的逻辑
        * 也是通过响应长按事件来实现
        * */
        /* 监听textListView的长按事件 */
        textListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                NoteEntry noteEntry = textNoteEntryList.get(position);
                if (!noteEntry.isSelected()){
                    textSelectedNumber++;
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.VISIBLE);
                    noteEntry.setSelected(true);
                }else {
                    textSelectedNumber--;
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.INVISIBLE);
                    noteEntry.setSelected(false);
                }
                if (textSelectedNumber > 0)
                    deleteNotebook.setVisibility(View.VISIBLE);
                else deleteNotebook.setVisibility(View.INVISIBLE);

                /* return true表示长按事件和点击事件不同时响应 */
                return true;
            }
        });

        pptListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                NoteEntry noteEntry = pptNoteEntryList.get(position);
                if (!noteEntry.isSelected()){
                    pptSelectedNumber++;
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.VISIBLE);
                    noteEntry.setSelected(true);
                }else {
                    pptSelectedNumber--;
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.INVISIBLE);
                    noteEntry.setSelected(false);
                }
                if (pptSelectedNumber > 0)
                    deleteNotebook.setVisibility(View.VISIBLE);
                else deleteNotebook.setVisibility(View.INVISIBLE);

                /* return true表示长按事件和点击事件不同时响应 */
                return true;
            }
        });

        cardListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                NoteEntry noteEntry = cardNoteEntryList.get(position);
                if (!noteEntry.isSelected()){
                    cardSelectedNumber++;
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.VISIBLE);
                    noteEntry.setSelected(true);
                }else {
                    cardSelectedNumber--;
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.INVISIBLE);
                    noteEntry.setSelected(false);
                }
                if (cardSelectedNumber > 0)
                    deleteNotebook.setVisibility(View.VISIBLE);
                else deleteNotebook.setVisibility(View.INVISIBLE);

                /* return true表示长按事件和点击事件不同时响应 */
                return true;
            }
        });
        ///////////////////////////////////////////////////////////////////////////////////////////////////////

        /* 监听deleteNotebook的点击事件
         * 实现从数据库中删除notebook，及其附属一切的逻辑
          * */
        deleteNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNotebook.setVisibility(View.INVISIBLE);
                NoteEntry noteEntry;
                switch (currentPageIndex){
                    case 0:
                        /* 删除操作的逻辑 */
                        if (textSelectedNumber > 0){
                            for (int i = textNoteEntryList.size()-1; i >= 0; i--){
                                noteEntry = textNoteEntryList.get(i);
                                if (noteEntry.isSelected()){
                                    /* UI上note entry的删除 */
                                    textNoteEntryList.remove(i);

                                    /* 删除数据库中的数据
                                     * 把和这个notebook所关联的所有数据都删除
                                      * */
                                    Notebook notebook = textNbs.remove(i);
                                    long notebookId = notebook.getId();
                                    if (notebook.isSaved()){
                                        notebook.delete();
                                        /* 删除所有的notes */
                                        List<Note> delNotes =
                                                DataSupport.where("notebookId == ?", notebookId+"").find(Note.class);
                                        for (Note note:delNotes){
                                            if (note.isSaved()){
                                                note.delete();
                                            }
                                        }
                                    }

                                    textSelectedNumber--;
                                }
                            }

                            /* 更新UI */
                            textAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 1:
                        /* 删除操作的逻辑 */
                        if (pptSelectedNumber > 0){
                            for (int i = pptNoteEntryList.size()-1; i >= 0; i--){
                                noteEntry = pptNoteEntryList.get(i);
                                if (noteEntry.isSelected()){
                                    /* UI上note entry的删除 */
                                    pptNoteEntryList.remove(i);

                                    /* 删除数据库中的数据
                                     * 把和这个notebook所关联的所有数据都删除
                                      * */
                                    Notebook notebook = pptNbs.remove(i);
                                    long notebookId = notebook.getId();
                                    if (notebook.isSaved()){
                                        notebook.delete();
                                        /* 删除所有的notes */
                                        List<Note> delNotes =
                                                DataSupport.where("notebookId == ?", notebookId+"").find(Note.class);
                                        for (Note note:delNotes){
                                            if (note.isSaved()){
                                                note.delete();
                                            }
                                        }
                                    }

                                    pptSelectedNumber--;
                                }
                            }

                            /* 更新UI */
                            pptAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        /* 删除操作的逻辑 */
                        if (cardSelectedNumber > 0){
                            for (int i = cardNoteEntryList.size()-1; i >= 0; i--){
                                noteEntry = cardNoteEntryList.get(i);
                                if (noteEntry.isSelected()){
                                    /* UI上note entry的删除 */
                                    cardNoteEntryList.remove(i);

                                    /* 删除数据库中的数据
                                     * 把和这个notebook所关联的所有数据都删除
                                      * */
                                    Notebook notebook = cardNbs.remove(i);
                                    long notebookId = notebook.getId();
                                    if (notebook.isSaved()){
                                        notebook.delete();
                                        /* 删除所有的notes */
                                        List<Note> delNotes =
                                                DataSupport.where("notebookId == ?", notebookId+"").find(Note.class);
                                        for (Note note:delNotes){
                                            if (note.isSaved()){
                                                note.delete();
                                            }
                                        }
                                    }

                                    cardSelectedNumber--;
                                }
                            }

                            /* 更新UI */
                            cardAdapter.notifyDataSetChanged();
                        }
                        break;
                }
            }
        });

        /* 启动拍照功能
         * 将当前的currentPageIndex传过去
          * */
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TakePhotoActivity.class);
                /* 传入currentPageIndex
                 * 这样就可以在创建一个笔记本的时候，知道这个笔记本的类型
                  * */
                intent.putExtra("currentPageIndex", currentPageIndex);
                intent.putExtra("currentUserId", currentUserId);
                startActivity(intent);
            }
        });

        /* 读取本地数据看是否已经保存了该用户的头像 */
        SharedPreferences read = getSharedPreferences("data", MODE_PRIVATE);
        write = read.edit();
        final boolean headPortraitSaved = read.getBoolean("headPortraitSaved", false);

        if (headPortraitSaved){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 3;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            headPortrait.setImageBitmap(bitmap);
        }

        /* 设置头像 */
        headPortrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0x1);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0x1){
            if (data != null){
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    headPortrait.setImageBitmap(bitmap);
                    FileOutputStream fos = new FileOutputStream(path);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 50, fos);
                    fos.flush();
                    fos.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
                write.putBoolean("headPortraitSaved", true);
                write.apply();
            }
        }
    }

    /* 从数据读入数据的初始化操作 */
    private void initEntry() {
        /* 从数据库读取notebook
        * 当前用户的
        * */
        nbs = DataSupport.where("userId == ?", currentUserId+"").find(Notebook.class);

        /* 局部变量，用于存储某个Notebook中所有的notes */
        List<Note> notes;
        Note firstNote;
        String path;
        Bitmap bitmap;

        /* 好的，现在读取笔记本 */
        for (Notebook nb: nbs){
            Log.w(TAG, "initEntry: "+nb.getName() + "\\"+nb.getType() + "\\" + nb.getId());
            /* 查询这个notebook中所有的notes */
            notes = DataSupport.where("notebookId == ?", ""+nb.getId()).find(Note.class);
            /* 取出第一条note
            *  如果有第一条note，那么根据这条note设置相应显示的图片
            * */
            if (notes.size() > 0) {
                firstNote = notes.get(0);
                path = firstNote.getPath();
                bitmap = BitmapFactory.decodeFile(path);
            }
            else{
                switch (nb.getType()){
                    case Notebook.NOTE_TYPE_TEXT:
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_text);
                        break;
                    case Notebook.NOTE_TYPE_PPT:
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_ppt);
                        break;
                    default:
                        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_card);
                        break;
                }
            }


            if (nb.getType() == Notebook.NOTE_TYPE_TEXT) {
                textNbs.add(nb);
                textNoteEntryList.add(new NoteEntry(nb.getName(), nb.getDate(), bitmap));
            }
            else if (nb.getType() == Notebook.NOTE_TYPE_PPT) {
                pptNbs.add(nb);
                pptNoteEntryList.add(new NoteEntry(nb.getName(), nb.getDate(), bitmap));
            }
            else if(nb.getType() == Notebook.NOTE_TYPE_CARD)  {
                cardNbs.add(nb);
                cardNoteEntryList.add(new NoteEntry(nb.getName(), nb.getDate(), bitmap));
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
                /* 将deleteNotebook这个悬浮按钮设置为不可见 */
                deleteNotebook.setVisibility(View.INVISIBLE);
                switch (position) {
                    case 0:
                        /* 清除当前page的所有notebook的选中状态 */
                        textSelectedNumber = 0;
                        // 现在需要获得list view中的所有view
                        for (int i = 0 ; i < textNoteEntryList.size(); i++){
                            /* 得到当前索引位置的view */
                            View view = textListView.getChildAt(i);
                            ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                            notebookIsSelected.setVisibility(View.INVISIBLE);
                            textNoteEntryList.get(i).setSelected(false);
                        }

                        toolbar.setTitle(R.string.text);
                        navigationView.getMenu().getItem(0).setChecked(true);
                        break;
                    case 1:
                        /* 清除当前page的所有notebook的选中状态 */
                        pptSelectedNumber = 0;
                        // 现在需要获得list view中的所有view
                        for (int i = 0 ; i < pptNoteEntryList.size(); i++){
                            /* 得到当前索引位置的view */
                            View view = pptListView.getChildAt(i);
                            ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                            notebookIsSelected.setVisibility(View.INVISIBLE);
                            pptNoteEntryList.get(i).setSelected(false);
                        }
                        toolbar.setTitle(R.string.ppt);
                        navigationView.getMenu().getItem(1).setChecked(true);
                        break;
                    case 2:
                        /* 清除当前page的所有notebook的选中状态 */
                        cardSelectedNumber = 0;
                        // 现在需要获得list view中的所有view
                        for (int i = 0 ; i < cardNoteEntryList.size(); i++){
                            /* 得到当前索引位置的view */
                            View view = cardListView.getChildAt(i);
                            ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                            notebookIsSelected.setVisibility(View.INVISIBLE);
                            cardNoteEntryList.get(i).setSelected(false);
                        }
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        deleteNotebook.setVisibility(View.INVISIBLE);
        switch (id) {
            case R.id.text_menu:
                currentPageIndex = 0;

                /* 清除当前page的所有notebook的选中状态 */
                textSelectedNumber = 0;
                // 现在需要获得list view中的所有view
                for (int i = 0 ; i < textNoteEntryList.size(); i++){
                    /* 得到当前索引位置的view */
                    View view = textListView.getChildAt(i);
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.INVISIBLE);
                    textNoteEntryList.get(i).setSelected(false);
                }

                viewPager.setCurrentItem(0);
                break;
            case R.id.ppt_menu:
                currentPageIndex = 1;

                /* 清除当前page的所有notebook的选中状态 */
                pptSelectedNumber = 0;
                // 现在需要获得list view中的所有view
                for (int i = 0 ; i < pptNoteEntryList.size(); i++){
                    /* 得到当前索引位置的view */
                    View view = pptListView.getChildAt(i);
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.INVISIBLE);
                    pptNoteEntryList.get(i).setSelected(false);
                }

                viewPager.setCurrentItem(1);
                break;
            case R.id.card_menu:
                currentPageIndex = 2;

                /* 清除当前page的所有notebook的选中状态 */
                cardSelectedNumber = 0;
                // 现在需要获得list view中的所有view
                for (int i = 0 ; i < cardNoteEntryList.size(); i++){
                    /* 得到当前索引位置的view */
                    View view = cardListView.getChildAt(i);
                    ImageView notebookIsSelected = view.findViewById(R.id.notebook_is_selected);
                    notebookIsSelected.setVisibility(View.INVISIBLE);
                    cardNoteEntryList.get(i).setSelected(false);
                }
                viewPager.setCurrentItem(2);
                break;
            case R.id.sign_out:
                /* 先从本地读取数据 */
                write.putBoolean("isLogin", false);
                write.putLong("currentUserId", currentUserId);
                write.putBoolean("headPortraitSaved", false);
                write.apply();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("fromMainActivity", true);
                startActivity(intent);
                MainActivity.this.finish();
                break;
            case R.id.user:
                break;
            default:
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
                setView(inputName).
                setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = inputName.getText().toString();
                Date date = new Date();
                /* 当创建成功时，修改相应的ui
                 * 并将创建好的notebook添加到数据库中
                  * */
                if (inputName.getText().toString().length() > 0) {
                    /* 为了设置notebook的id
                    * 必须先得到所有的notebooks
                    * */
                    long id = 1;
                    List<Notebook> notebooks = DataSupport.findAll(Notebook.class);
                    if (notebooks.size()>0)
                        id = notebooks.get(notebooks.size()-1).getId()+1;
                    Notebook nb = new Notebook(name,date.toString(), currentPageIndex);
                    NoteEntry entry;
                    nb.setId(id);
                    nb.setUserId(currentUserId);
                    nb.save();
                    switch (currentPageIndex){
                        case 0:
                            textNbs.add(nb);
                            entry = new NoteEntry(name, date.toString(),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_text));
                            textNoteEntryList.add(entry);
                            textAdapter.notifyDataSetChanged();
                            break;
                        case 1:
                            pptNbs.add(nb);
                            entry = new NoteEntry(name, date.toString(),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_ppt));
                            pptNoteEntryList.add(entry);
                            pptAdapter.notifyDataSetChanged();
                            break;
                        case 2:
                            cardNbs.add(nb);
                            entry = new NoteEntry(name, date.toString(),
                                    BitmapFactory.decodeResource(getResources(), R.drawable.ic_card));
                            cardNoteEntryList.add(entry);
                            cardAdapter.notifyDataSetChanged();
                            break;
                        default:
                            break;
                    }
                }
                else Toast.makeText(MainActivity.this, "创建失败", Toast.LENGTH_SHORT).show();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onRestart() {
//        当activity到达前台时，onRestart生命周期函数最开始被调用
        /* 在restart中只要再次查询数据库，重新init一次就好了 */
        super.onRestart();

        /* 先将几个全局list清空 */
        for (int i = textNbs.size()-1; i >= 0 ; i-- ) {
            textNoteEntryList.remove(i);
            textNbs.remove(i);
        }
        for (int i = pptNbs.size()-1; i >= 0 ; i-- ) {
            pptNoteEntryList.remove(i);
            pptNbs.remove(i);
        }
        for (int i = cardNbs.size()-1; i >= 0 ; i-- ) {
            cardNoteEntryList.remove(i);
            cardNbs.remove(i);
        }

        /* 调用initEntry
         * 从数据库中再次读入数据
         * 然后更新ui
          * */
        initEntry();
        textAdapter.notifyDataSetChanged();
        pptAdapter.notifyDataSetChanged();
        cardAdapter.notifyDataSetChanged();
    }
}
