package com.example.liuhui.photonote;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class ViewNotebookActivity extends AppCompatActivity {

    private final int REQUEST_PHOTO_FROM_TAKE_PHOTO = 0;

    private final String TAG = "ViewNotebookActivity";

//    记录当前所有笔记的array list
    private ArrayList<NoteView> notes = new ArrayList<>();

//    根据传入的path创建bitmap对象链表
    private ArrayList<Bitmap> savedBitmaps = new ArrayList<>();

//    记录当前所有的paths
    private ArrayList<String> paths = new ArrayList<>();

    private GridView noteGridView;

    private ArrayAdapter notesAdapter = null;

    private FloatingActionButton takePhoto;

    private FloatingActionButton deleteNote;

    private int selectedNoteNumber = 0;

    private boolean fromMain = false;

    private long id = 0;

    private long dirId = 0;

    private int type = 0;

    private String name = " ";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notebook);
        fromMain = getIntent().getBooleanExtra("fromMain", false);
        if (!fromMain)
            paths = getIntent().getStringArrayListExtra("paths");
        id = getIntent().getLongExtra("id", 0);
        dirId = getIntent().getLongExtra("dirId", 0);
        type = getIntent().getIntExtra("type", 0);
        name = getIntent().getStringExtra("name");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (name.equals(" "))
            toolbar.setTitle("新建笔记");
        else
            toolbar.setTitle(name);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog();
            }
        });

//        根据传入的paths创建bitmap对象链表
        for (String path:paths
             ) {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            savedBitmaps.add(bitmap);
        }

//        得到布局中的note grid view
        noteGridView = (GridView) findViewById(R.id.note_grid_view);
//        得到布局中的take photo
        takePhoto = (FloatingActionButton) findViewById(R.id.take_photo);
//        得到布局中的delete note
        deleteNote = (FloatingActionButton) findViewById(R.id.delete_note); 

//        初始化notebook
        initNotebook();

        noteGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                item 被点击的时候，activity切换到ViewNoteActivity
                Intent intent = new Intent(ViewNotebookActivity.this, ViewNoteActivity.class);
                intent.putExtra("paths", paths);
                startActivity(intent);
            }
        });

        noteGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                切换noteView的选中状态
                NoteView noteView = notes.get(position);
                if (!noteView.isSelected()){
                    selectedNoteNumber++;
                    ImageView noteIsSelected = view.findViewById(R.id.note_is_selected);
                    noteIsSelected.setVisibility(View.VISIBLE);
                    noteView.setSelected(true);
                }else {
                    selectedNoteNumber--;
                    ImageView noteIsSelected = view.findViewById(R.id.note_is_selected);
                    noteIsSelected.setVisibility(View.INVISIBLE);
                    noteView.setSelected(false);
                }
                if (selectedNoteNumber > 0)
                    deleteNote.setVisibility(View.VISIBLE);
                else deleteNote.setVisibility(View.INVISIBLE);
                return false;
            }
        });
        
//        监听deleteNote的点击事件
        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteView noteView;
                if (selectedNoteNumber > 0) {
//                    删除notes中应该删除的元素和paths中的路径
                    for(int i = 0 ; i < notes.size(); i++){
                        noteView = notes.get(i);
//                        重新设置noteView中的noteNumber
                        noteView.setNoteNumber(i+1);
                        if (noteView.isSelected()){
                            notes.remove(i);
                            paths.remove(i);
                            i--;
                            selectedNoteNumber--;
                        }
                    }
//                    刷新notes adapter使得删除在UI上显示
                    notesAdapter.notifyDataSetChanged();
                }

//                如果selectedNoteNumber <= 0隐藏deleteNote
                if (selectedNoteNumber <= 0)
                    deleteNote.setVisibility(View.INVISIBLE);
            }
        });

        //        监听takePhoto的点击事件
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNotebookActivity.this, TakePhotoActivity.class);
                intent.putExtra("from_view_notebook", true);
                startActivityForResult(intent, REQUEST_PHOTO_FROM_TAKE_PHOTO);
            }
        });
    }


    private void initNotebook(){
        int size = savedBitmaps.size();

        for (int i = 0 ; i < size ; i++)
            notes.add(new NoteView(i+1, savedBitmaps.get(i), paths.get(i)));

        notesAdapter = new NoteViewAdapter(ViewNotebookActivity.this,
                R.layout.note_view_container, notes);
        noteGridView.setAdapter(notesAdapter);
    }

    /**
     * @param name 设置新笔记本的名称
     */
    private void setNewNotebookName(String name) {
        MainActivity.newNotebookName = name;
    }

    @Override
    public void onBackPressed() {
//        当后退键按下时，应该向main activity传递一些信息
//        在这里使用static 全局变量来传递新笔记本的名称
        if (name.equals(" ")) {
            show_dialog();
            name = toolbar.getTitle().toString();
            setNewNotebookName(name);
        }
        else if (fromMain){
            super.onBackPressed();
        }
        else {
            setNewNotebookName(name);
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_PHOTO_FROM_TAKE_PHOTO:
                if (resultCode == RESULT_OK){
//                    从take photo activity中得到新的photo
                    ArrayList<String> newPaths = data.getStringArrayListExtra("new_paths");
                    if (newPaths!=null && newPaths.size() > 0) {
                        int currentNumber = paths.size();
                        Bitmap bitmap;

//                    将相关数据添加到相应的集合中
                        for (String path : newPaths
                                ) {
                            paths.add(path);
                            bitmap = BitmapFactory.decodeFile(path);
                            notes.add(new NoteView(currentNumber+1, bitmap, path));
                            currentNumber++;
                        }

//                        更新UI
                        notesAdapter.notifyDataSetChanged();
                    }
                }
                break;
            default:
                break;
        }
    }

    private void show_dialog(){
        final EditText inputName = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("输入笔记本名称哈").setView(inputName).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(ViewNotebookActivity.this, "取消输入", Toast.LENGTH_SHORT).show();
            }
        }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputN = inputName.getText().toString();
                if (inputN.length() > 0) {
                    name = inputN;
                    toolbar.setTitle(inputN);
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
