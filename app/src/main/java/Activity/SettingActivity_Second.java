package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class SettingActivity_Second extends AppCompatActivity {
    //这是从主界面进来的设置界面1
    private TextView main_username;
    private String str_username;
    private byte[]avatar;
    private ImageView main_avatar;
    private ImageView img_back_to_main;
    private TextView main_signature;
    private String str_signature;
    private SharedPreferences sp;//读取qq
    private int main_qq;
    private UserDbHelper db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_second);
        main_username=findViewById(R.id.main_username);
        main_avatar=findViewById(R.id.main_avatar);
        main_signature=findViewById(R.id.main_signature);
        img_back_to_main=findViewById(R.id.img_back_to_main);

        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        db= UserDbHelper.getInstance(this);
        //设置头像
        avatar=db.getAvatar(main_qq);
        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        main_avatar.setImageBitmap(bitmap);
        //设置名字
        str_username=db.getUsername(main_qq);
        main_username.setText(str_username);
        //设置个性签名
        str_signature=db.getSignature(main_qq);
        main_signature.setText(str_signature);
        //返回主界面
        img_back_to_main.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity_Second.this,MainActivity_Second.class));
        });
    }


    public void Into_Main_Info(View view){
        startActivity(new Intent(this,Main_Info.class));
    }
    public void Into_Setting_Second(View view){
        startActivity(new Intent(SettingActivity_Second.this,SettingActivity_Third.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, MainActivity_Second.class));
    }
}