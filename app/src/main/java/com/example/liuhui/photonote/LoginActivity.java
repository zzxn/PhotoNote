package com.example.liuhui.photonote;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.util.List;

import site.gemus.openingstartanimation.OpeningStartAnimation;
import site.gemus.openingstartanimation.RedYellowBlueDrawStrategy;

public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button signIn;
    private Button signUp;

    private String TAG = "LoginActivity";

    private boolean isLogin = false;
    private long currentUserId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new OpeningStartAnimation.Builder(this).setDrawStategy(new RedYellowBlueDrawStrategy())
                .setAnimationInterval(3850).setAnimationFinishTime(450).setAppStatement("Photo Note")
                .create().show(this);

        /* 先从本地读取数据 */
        SharedPreferences read = getSharedPreferences("data", MODE_PRIVATE);
        final SharedPreferences.Editor write = read.edit();

        isLogin = read.getBoolean("isLogin", false);
        currentUserId = read.getLong("currentUserId", 0);

        /* 如果是login，则直接跳转到MainActivity
         * 并将currentId传给MainActivity
          * */
        if (isLogin){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("currentUserId", currentUserId);
        }

        setContentView(R.layout.activity_login);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        signIn = (Button) findViewById(R.id.sign_in);
        signUp = (Button) findViewById(R.id.sign_up);

        /* 登陆的按钮 */
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String passw = password.getText().toString();
                if (uname.length() == 0)
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                else if (passw.length() == 0)
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                else {
                    List<User> users = DataSupport.
                            where("username == ?", uname).where("password = ?", passw).find(User.class);
                    if (users.size() == 0)
                        Toast.makeText(LoginActivity.this, "用户名或者密码错误", Toast.LENGTH_SHORT).show();
                    /* 登陆成功
                     * 保存相应的数据之后，跳转到MainActivity
                      * */
                    else {
                        isLogin = true;
                        currentUserId = users.get(0).getId();
                        write.putBoolean("isLogin", isLogin);
                        write.putLong("currentUserId", currentUserId);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("currentUserId", currentUserId);
                    }
                }
            }
        });

        /* 注册的按钮 */
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uname = username.getText().toString();
                String passw = password.getText().toString();
                if (uname.length() == 0)
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                else if (passw.length() == 0)
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                else {
                   User user = new User(uname, passw);
                   user.save();
                   Log.w(TAG, "onClick: user id:"+user.getId());

                   /* 将isLogin和currentUserId保存到本地 */
                    isLogin = true;
                    currentUserId = user.getId();
                    write.putBoolean("isLogin", isLogin);
                    write.putLong("currentUserId", currentUserId);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("currentUserId", currentUserId);
                }
            }
        });
    }
}
