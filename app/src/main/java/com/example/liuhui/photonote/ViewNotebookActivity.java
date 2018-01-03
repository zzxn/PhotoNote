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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewNotebookActivity extends AppCompatActivity {

    private final int REQUEST_PHOTO_FROM_TAKE_PHOTO = 0;

    private final String TAG = "ViewNotebookActivity";

//    记录当前所有笔记的array list
    private ArrayList<NoteView> notes = new ArrayList<>();

//    记录当前所有数据库中note的array list
    private ArrayList<Note> databaseNotes = new ArrayList<>();

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

    private int type = 0;

    private String name = "";

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notebook);

        /* 判断是否是从MainActivity启动 */
        fromMain = getIntent().getBooleanExtra("fromMain", false);

        /* 不是从MainActivity启动，则是从TakePhotoActivity启动，这个notebook是一个新建的notebook
         * 得到notes的path,以及notebook的类型(currentPageIndex)
          * */
        if (!fromMain){
            paths = getIntent().getStringArrayListExtra("paths");
            type = getIntent().getIntExtra("currentPageIndex", 0);
            name = "新笔记";

            /* 将这个新建的notebook保存 */
            Notebook notebook = new Notebook();
            Date date = new Date();
            notebook.setName(name);
            notebook.setType(type);
            notebook.setDate(date.toString());

            /* 为了设置notebook的id
             * 必须先得到所有的notebooks
              * */
            List<Notebook> notebooks = DataSupport.findAll(Notebook.class);
            if (notebooks.size()>0)
                notebook.setId(notebooks.get(notebooks.size()-1).getId()+1);
            else
                notebook.setId(1);

            notebook.save();
            /* 得到当前notebook的id */
            id = notebook.getId();
            Log.w(TAG, "onCreate: notebook id"+id);

            /* 计算一下现在数据库中最后一个note的id */
            List<Note> notes2 = DataSupport.findAll(Note.class);
            long lastId = 1;
            if (notes2.size() > 1)
                lastId = notes2.get(notes2.size()-1).getId()+1;

            /* 将这个notebook的所有notes保存 */
            for (String path: paths){
                Note note = new Note();
                note.setPath(path);
                note.setNotebookId(notebook.getId());
                note.setId(lastId);
                note.save();
                lastId++;
                databaseNotes.add(note);
            }
        }
        /* 否则这个notebook不是一个新建的notebook
         * 得到这个notebook的name和id
          * */
        else {
            name = getIntent().getStringExtra("name");
            id = getIntent().getLongExtra("id", 0);
            Log.w(TAG, "onCreate: notebook id"+id);

            /* 找到这个notebook所有的notes */
            List<Note> notes1 = DataSupport.where("notebookId == ?", id+"").find(Note.class);
            /* 然后将note的path添加到paths中 */
            for (Note note: notes1){
                databaseNotes.add(note);
                paths.add(note.getPath());
            }
        }

        /* 监听toolbar的点击事件
         * 点击toolbar是为了修改notebook的名字
          * */
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(name);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_dialog();
            }
        });

//        根据paths创建bitmap对象链表
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

        /* 监听note的点击事件 */
        noteGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                item 被点击的时候，activity切换到ViewNoteActivity
                Intent intent = new Intent(ViewNotebookActivity.this, ViewNoteActivity.class);

                /* 使用bundle
                 * 将note对象构成的array list传给ViewNoteActivity
                  * */
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("notes", databaseNotes);
                intent.putExtras(bundle);
                intent.putExtra("currentIndex", i);
                intent.putExtra("paths", paths);
                Log.w(TAG, "onItemClick: put extra note");
                startActivity(intent);
            }
        });

        /* 监听note的长按事件 */
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
                /* return true
                 * 表示长按事件和点击事件不同时响应
                  * */
                return true;
            }
        });
        
//        监听deleteNote的点击事件
        /* 从数据库删除note的逻辑还没写 */
        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteView noteView;
                if (selectedNoteNumber > 0) {
//                    删除notes中应该删除的元素和paths中的路径
//                    外加删除数据库中的note
                    for(int i = 0 ; i < notes.size(); i++){
                        noteView = notes.get(i);
//                        重新设置noteView中的noteNumber
                        noteView.setNoteNumber(i+1);
                        if (noteView.isSelected()){
                            notes.remove(i);
                            paths.remove(i);

                            /* 现在删除数据库中的note和其中存储的marks */
                            Note note = databaseNotes.remove(i);
                            long noteId = note.getId();
                            /* 删除note */
                            if (note.isSaved()) {
                                Log.w(TAG, "onClick: delete a note and it's marks");
                                note.delete();
                                /* 删除与之关联的marks */
                                DataSupport.deleteAll(Mark.class, "noteId = ?", noteId + "");
                            }
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


    /* 初始化，更新ui */
    private void initNotebook(){
        int size = savedBitmaps.size();

        for (int i = 0 ; i < size ; i++)
            notes.add(new NoteView(i+1, savedBitmaps.get(i), paths.get(i)));

        notesAdapter = new NoteViewAdapter(ViewNotebookActivity.this,
                R.layout.note_view_container, notes);
        noteGridView.setAdapter(notesAdapter);
    }

    @Override
    public void onBackPressed() {
        /* 现在改成不执行任何额外的逻辑
         * 只是简单地调用super.onBackPressed
          * */
        super.onBackPressed();
    }

    /*
    * 重载onActivityResult方法
    * 用于接收从TakePhotoActivity中拍摄的新的notes
    * 更新ui
    * 并将这些新的notes保存
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_PHOTO_FROM_TAKE_PHOTO:
                if (resultCode == RESULT_OK){
//                    从take photo activity中得到新的photo
                    ArrayList<String> newPaths = data.getStringArrayListExtra("newPaths");
                    if (newPaths!=null && newPaths.size() > 0) {
                        int currentNumber = paths.size();
                        Bitmap bitmap;

                        List<Note> dNotes = DataSupport.findAll(Note.class);
                        long lastId = 1;
                        if (dNotes.size() > 1)
                            lastId = dNotes.get(dNotes.size()-1).getId()+1;

//                    将相关数据添加到相应的集合中
                        for (String path : newPaths
                                ) {
                            /* 这里是UI方面的逻辑 */
                            paths.add(path);
                            bitmap = BitmapFactory.decodeFile(path);
                            notes.add(new NoteView(currentNumber+1, bitmap, path));
                            currentNumber++;

                            /* 将新拍摄的note储存到数据库中 */

                            Note note = new Note();
                            note.setPath(path);
                            note.setNotebookId(id);
                            note.setId(lastId);
                            note.save();

                            lastId++;
                            databaseNotes.add(note);
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

    /* 用于得到用户输入的新的notebook的名字
     * 如果那么改变了，那么便更新数据库中的数据
      * */
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
                if (inputN.length() > 0){
                    toolbar.setTitle(inputN);
                    name = inputN;
                    Toast.makeText(ViewNotebookActivity.this, name, Toast.LENGTH_SHORT).show();
                    /* 更新notebook的name */
                    Notebook notebook = new Notebook();
                    notebook.setName(name);
                    notebook.update(id);
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
