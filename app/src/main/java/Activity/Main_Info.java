package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pretend_qq.R;

import java.time.LocalDate;
import java.time.Period;

import SQLite.UserDbHelper;

public class Main_Info extends AppCompatActivity {
    private UserDbHelper db;
    private SharedPreferences sp;
    private int main_qq;
    private ImageView img_back;
    private ImageView img_setting;
    private ImageView main_avatar;//头像
    private TextView main_username;//用户名
    private TextView tv_main_qq;//QQ号
    private int age;//年龄
    private int year;
    private int month;
    private int day;
    private String city;
    private TextView tv_age;
    private TextView tv_month;
    private TextView tv_day;
    private TextView tv_city;
    private TextView tv_signature;
    private Button revise_message;
    private byte[]avatar;
    private String str_main_username;
    private String str_signature;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_info);
        db= UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        //初始化控件
        main_avatar=findViewById(R.id.main_avatar);
        main_username=findViewById(R.id.main_username);
        tv_main_qq=findViewById(R.id.tv_main_qq);
        img_back=findViewById(R.id.img_back);
        img_setting=findViewById(R.id.img_setting);
        tv_month=findViewById(R.id.tv_month);
        tv_day=findViewById(R.id.tv_day);
        tv_city=findViewById(R.id.tv_city);
        tv_age=findViewById(R.id.tv_age);
        tv_signature=findViewById(R.id.tv_signature);
        revise_message=findViewById(R.id.revise_message);
        //读取信息并设置到控件上
        avatar=db.getAvatar(main_qq);
        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        main_avatar.setImageBitmap(bitmap);

        str_main_username=db.getUsername(main_qq);
        main_username.setText(str_main_username);
        tv_main_qq.setText(String.valueOf(main_qq));

        year=db.getYear(main_qq);
        month=db.getMonth(main_qq);
        day=db.getDay(main_qq);
        city=db.getRegion(main_qq);
        age=getAge(year,month,day);
        str_signature=db.getSignature(main_qq);

        tv_age.setText(String.valueOf(age));
        tv_month.setText(String.valueOf(month));
        tv_day.setText(String.valueOf(day));
        tv_city.setText(city);
        tv_signature.setText(str_signature);
        main_avatar.setOnClickListener(view -> {
            Intent intent=new Intent(Main_Info.this,ViewAvatarActivity.class);
            intent.putExtra("identification","user");
            startActivity(intent);
        });


        //返回按钮
        img_back.setOnClickListener(view -> {
            startActivity(new Intent(Main_Info.this,MainActivity_Second.class));
        });
        //新的设置界面
        img_setting.setOnClickListener(view -> {

        });
        //编辑资料按钮=>编辑资料界面
        revise_message.setOnClickListener(view -> {
            startActivity(new Intent(Main_Info.this, Revise_Message_Activity.class));
        });

    }
    //定义一个函数来根据生日的年月日返回当前的年龄
    public int getAge(int year, int month, int day) {
        //获取当前的日期
        LocalDate today = LocalDate.now();
        //获取生日的日期
        LocalDate birthday = LocalDate.of(year, month, day);
        //计算两个日期之间的时间间隔
        Period period = Period.between(birthday, today);
        //返回时间间隔中的年数
        return period.getYears();
    }
    public void go_to_revise_signature(View view){
          startActivity(new Intent(Main_Info.this, Revise_Signature_Activity.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, MainActivity_Second.class));
    }

}