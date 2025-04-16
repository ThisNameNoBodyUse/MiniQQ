package Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;
public class Account_Control_Activity extends AppCompatActivity {
    private UserDbHelper db;
    private SharedPreferences sp;
    private int main_qq;
    private String str_username;
    private byte[]avatar;
    private ImageView img_back;//返回到Setting_Third
    private ImageView main_avatar;
    private TextView main_username;
    private TextView tv_main_qq;
    private LinearLayout exit_login;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_control);
        //初始化控件
        img_back=findViewById(R.id.img_back);
        main_avatar=findViewById(R.id.main_avatar);
        main_username=findViewById(R.id.main_username);
        tv_main_qq=findViewById(R.id.tv_main_qq);
        exit_login=findViewById(R.id.exit_login);
        //初始化操作
        db= UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        //获取qq
        main_qq=sp.getInt("main_qq",0);
        //获取几个基本信息
        str_username=db.getUsername(main_qq);
        avatar=db.getAvatar(main_qq);
        //设置控件
        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        main_avatar.setImageBitmap(bitmap);

        main_username.setText(str_username);
        tv_main_qq.setText(String.valueOf(main_qq));
        //退出登录
        exit_login.setOnClickListener(view -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(Account_Control_Activity.this);
            builder.setTitle("退出登录");
            builder.setMessage("您确定要退出登录吗?");
            builder.setPositiveButton("是", (dialogInterface, i) -> {
                startActivity(new Intent(Account_Control_Activity.this, LoginActivity_Second.class));
            });
            builder.setNegativeButton("否", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
            builder.create().show();
        });
    }
    public void to_Setting_Third(View view){
        //返回Setting_Third
        startActivity(new Intent(Account_Control_Activity.this,SettingActivity_Third.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, SettingActivity_Third.class));
    }

}