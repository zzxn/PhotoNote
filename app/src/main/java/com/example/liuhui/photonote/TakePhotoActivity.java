package com.example.liuhui.photonote;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.pqpo.smartcropperlib.view.CropImageView;

public class TakePhotoActivity extends AppCompatActivity {
    private int REQUEST_CODE_TAKE_PHOTO = 1;

    private final String TAG = "TakePhotoActivity";

//    用于裁剪photo的对象
    CropImageView imageCrop;

//    保存photo的临时文件
    File tempFile;

//    拍摄photo的image view
    ImageView takePhoto;

//    保存photo的image view
    ImageView save;

//    丢弃当前拍摄photo的image view
    ImageView drop;

//    退出当前activity，进入view notebook activity的image view
    ImageView exit;

//    显示保存过的photo的image view
    ImageView prePhoto;

//    当前已经保存过的photo的数量
    TextView photoNumber;

//    指示当前photo是否已经保存
    boolean alreadySaved = false;

//    指示当前photo是否已经丢弃
    boolean alreadyDropped = false;

//    指示当前photo的索引
    int index = 1;
//    当前已经保存的photo的数量
    int count = 0;

//    指示上一级activity是否为ViewNotebookActivity
    boolean fromViewNotebook = false;

//    如果上一级activity是MainActivity，currentPageIndex指示当前笔记本的类型
    int currentPageIndex = 0;

//    已保存photo的path
    ArrayList<String> paths = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_photo);
        fromViewNotebook = getIntent().getBooleanExtra("from_view_notebook", false);

        /* 如果不是fromViewNotebook
         * 那么获得当前笔记本的类型
          * */
        if (!fromViewNotebook)
            currentPageIndex = getIntent().getIntExtra("currentPageIndex", 0);

        ////////////////////////////////////////////////////////////////////////////////////////////
        /// 一系列前期处理操作
        /* 从布局文件中得到必要的组件 */
        imageCrop = (CropImageView) findViewById(R.id.image_crop);

        takePhoto = (ImageView) findViewById(R.id.take_photo);

        save = (ImageView) findViewById(R.id.save);

        drop = (ImageView) findViewById(R.id.drop);

        exit = (ImageView) findViewById(R.id.exit);

        prePhoto = (ImageView) findViewById(R.id.pre_photo);

        photoNumber = (TextView) findViewById(R.id.photo_number);

        /* 得到数据库中所有的note的数目
         * litepal默认是按照id的大小排序
         * 然后设置index的值
          * */
        List<Note> notes = DataSupport.findAll(Note.class);
        if (notes.size() > 0)
            index = (int)(notes.get(notes.size()-1).getId()+1);

        tempFile = new File(getExternalFilesDir("img"), "temp.jpg");
        /// 前期处理操作完毕
        ////////////////////////////////////////////////////////////////////////////////////////////

//        刚进入时直接启动拍摄功能
        startCamera();

//        如果点击take photo按钮，也启动拍摄功能
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });

//        保存photo
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageCrop.canRightCrop() && !alreadySaved && !alreadyDropped){
                    photoNumber.setVisibility(View.VISIBLE);
                    alreadySaved = true;
                    Bitmap bitmap = imageCrop.crop();

                    try {
                        /* 这里只是暂时保存photo到本地
                         * 并没有将数据保存到数据库中
                         * 相应的path是可以在下一次被覆盖的
                          * */
                        File savedPhoto = new File(getExternalFilesDir("img"), "saved"+index+".jpg");
                        paths.add(savedPhoto.getAbsolutePath());
                        index++;
                        count++;
                        FileOutputStream fos = new FileOutputStream(savedPhoto);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                    }catch (IOException e){
                        e.printStackTrace();
                    }

                    prePhoto.setImageBitmap(bitmap);
                    photoNumber.setText(count+"");
                    Toast.makeText(TakePhotoActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                }
                else if (alreadySaved)
                    Toast.makeText(TakePhotoActivity.this, "已经保存", Toast.LENGTH_SHORT).show();
                else if (alreadyDropped)
                    Toast.makeText(TakePhotoActivity.this, "已经丢弃", Toast.LENGTH_SHORT).show();
            }
        });

//        丢弃当前photo
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* 设置已经丢弃 */
                alreadyDropped = true;
                Toast.makeText(TakePhotoActivity.this, "丢弃笔记", Toast.LENGTH_SHORT).show();
            }
        });

        /* 当退出imageView被点击的时候，进入到ViewNotebookActivity */
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turnToViewNotebook();
            }
        });
    }

    /**
     * turnToViewNotebook 从当前activity转到viewNotebookActivity
     */
    private void turnToViewNotebook(){
        if(!fromViewNotebook) {
//            如果上一级activity是MainActivity，则启动ViewNotebookActivity
//            将paths和currentPageIndex传给ViewNotebookActivity
            Intent intent = new Intent(TakePhotoActivity.this, ViewNotebookActivity.class);
            intent.putExtra("paths", paths);
            intent.putExtra("currentPageIndex", currentPageIndex);
            startActivity(intent);
        }else {
//            如果上一级activity是ViewNotebookActivity，则设置result
//            将新的paths传给ViewNotebookActivity
            Intent intent = new Intent();
            intent.putExtra("newPaths", paths);
            setResult(RESULT_OK, intent);
        }
//        结束当前activity
//        将TakePhotoActivity从栈中删除
        TakePhotoActivity.this.finish();
    }

    /**
     * startCamera 启动相机，并将tempFile的uri传入
     */
    private void startCamera(){
        alreadySaved = false;
        alreadyDropped = false;
        Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        if (startCameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
        }
    }

    /**
     * @param requestCode  请求代码
     * @param resultCode 结果代码
     * @param data 返回数据
     *             把tempFile中的图片文件解析成bitmap
     *             然后调用CropImageView的setImageToCrop函数
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            setResult(RESULT_CANCELED);
            finish();
        }

        Bitmap bi = null;

        if (requestCode == REQUEST_CODE_TAKE_PHOTO && tempFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(options);
            bi = BitmapFactory.decodeFile(tempFile.getAbsolutePath(), options);
        }

        if (bi != null)
            imageCrop.setImageToCrop(bi);
    }

    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    /**
     * onBackPressed 当后退键点击时，询问用户是否创建新的文件
     */
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);

        if (!fromViewNotebook)
            builder.setTitle("创建新笔记本").setMessage("是否为拍摄的照片创建新笔记本？");
        else
            builder.setTitle("保存当前笔记").setMessage("是否保存当前拍摄的笔记？");

        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        turnToViewNotebook();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        结束当前activity
//                        返回main activity
                        if (!fromViewNotebook)
                            TakePhotoActivity.super.onBackPressed();
                        else {
//                            设置paths是一个空的array list
                            paths = new ArrayList<>();
                            turnToViewNotebook();
                        }
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
