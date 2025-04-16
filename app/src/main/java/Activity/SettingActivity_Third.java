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

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class SettingActivity_Third extends AppCompatActivity {
    //这是从设置界面1左下角进来的设置界面2

    private ImageView user_avatar;
    private ImageView back_to_main;
    private byte[]avatar;
    private UserDbHelper db;
    private int main_qq;
    private SharedPreferences sp;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_third);
        user_avatar=findViewById(R.id.user_avatar);

        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        db=UserDbHelper.getInstance(this);
        main_qq=sp.getInt("main_qq",0);//主qq

        avatar=db.getAvatar(main_qq);
        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        user_avatar.setImageBitmap(bitmap);

        back_to_main=findViewById(R.id.img_back);
        //返回主活动
        back_to_main.setOnClickListener(view -> {
            startActivity(new Intent(SettingActivity_Third.this,MainActivity_Second.class));
        });

    }
    public void to_control_account(View view){
        startActivity(new Intent(SettingActivity_Third.this,Account_Control_Activity.class));
    }
    public void to_account_safe(View view){
        startActivity(new Intent(SettingActivity_Third.this,Account_Safe_Activity.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, MainActivity_Second.class));
    }
}