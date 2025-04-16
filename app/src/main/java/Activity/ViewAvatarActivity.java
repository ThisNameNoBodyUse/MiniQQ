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

public class ViewAvatarActivity extends AppCompatActivity {
    //这个活动用于显示头像,放大版
    private ImageView img_back;//返回
    private ImageView img_avatar;//头像显示
    private UserDbHelper db;
    private SharedPreferences sp;
    private int friend_qq;
    private int main_qq;
    private byte[]avatar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_avatar);
        Intent intent=getIntent();
        String identification=intent.getStringExtra("identification");
        img_avatar=findViewById(R.id.img_avatar);
        img_back=findViewById(R.id.img_back);
        img_back.setOnClickListener(view -> {
            finish();
        });

        db=UserDbHelper.getInstance(this);
        if(identification.equals("user")){
            sp=getSharedPreferences("user_data",MODE_PRIVATE);
            main_qq=sp.getInt("main_qq",0);
            avatar=db.getAvatar(main_qq);
        }else{
            sp=getSharedPreferences("friend_data",MODE_PRIVATE);
            friend_qq=sp.getInt("friend_qq",0);
            avatar=db.getAvatar(friend_qq);
        }

        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        img_avatar.setImageBitmap(bitmap);

    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        finish();
    }
}