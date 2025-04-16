package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pretend_qq.R;

import java.time.LocalDate;
import java.time.Period;

import SQLite.UserDbHelper;

public class Friend_Info_Activity extends AppCompatActivity {
    private UserDbHelper db;
    private SharedPreferences sp;
    private String friend_signature;
    private int friend_qq;
    private String friend_name;//昵称
    private String gender;//性别
    private int year;
    private int month;
    private int day;
    private int age;
    private String city;
    private String signature;
    private byte[]avatar;
    //控件
    private ImageView friend_avatar;//好友头像
    private TextView friend_username;//好友昵称
    private TextView tv_friend_qq;//好友qq
    private ImageView ig_gender;//性别图标
    private TextView tv_gender;//好友性别
    private TextView tv_age;//好友年龄
    private TextView tv_month;//月
    private TextView tv_day;//日
    private TextView tv_city;//城市
    private TextView tv_signature;//签名
    private TextView tv_gender1;//另一个性别文字


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //好友信息主界面
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_info);
        //初始化控件等
        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("friend_data",MODE_PRIVATE);
        friend_qq=sp.getInt("friend_qq",0);//核心

        friend_avatar=findViewById(R.id.friend_avatar);
        friend_username=findViewById(R.id.friend_username);
        tv_friend_qq=findViewById(R.id.tv_friend_qq);
        ig_gender=findViewById(R.id.ig_gender);
        tv_gender=findViewById(R.id.tv_gender);
        tv_age=findViewById(R.id.tv_age);
        tv_month=findViewById(R.id.tv_month);
        tv_day=findViewById(R.id.tv_day);
        tv_city=findViewById(R.id.tv_city);
        tv_signature=findViewById(R.id.tv_signature);
        tv_gender1=findViewById(R.id.tv_gender1);

        //获取基本信息
        friend_name=db.getUsername(friend_qq);//昵称
        gender=db.getGender(friend_qq);
        year=db.getYear(friend_qq);
        month=db.getMonth(friend_qq);
        day=db.getDay(friend_qq);
        signature=db.getSignature(friend_qq);
        city=db.getRegion(friend_qq);
        avatar=db.getAvatar(friend_qq);
        //计算年龄
        age=getAge(year,month,day);
        //信息设置到控件上

        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        friend_avatar.setImageBitmap(bitmap);

        if(gender.equals("男")){
            ig_gender.setImageResource(R.mipmap.boy);
            tv_gender1.setText("他");
        }else{
            ig_gender.setImageResource(R.mipmap.girl);
            tv_gender1.setText("她");
        }


        tv_gender.setText(gender);
        tv_signature.setText(signature);
        tv_age.setText(String.valueOf(age));
        tv_month.setText(String.valueOf(month));
        tv_day.setText(String.valueOf(day));
        friend_username.setText(friend_name);
        tv_friend_qq.setText(String.valueOf(friend_qq));
        tv_city.setText(city);

        friend_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Friend_Info_Activity.this, ViewAvatarActivity.class);
                intent.putExtra("identification","friend");
                startActivity(intent);
            }
        });

    }
    public void go_to_chat(View view){
        //前往聊天界面
        startActivity(new Intent(Friend_Info_Activity.this,ChatActivity.class));
    }
    public void go_to_chat_record(View view){
        //返回聊天设置
        startActivity(new Intent(Friend_Info_Activity.this, ChatRecordActivity.class));
    }
    public void go_to_detail(View view){
        startActivity(new Intent(Friend_Info_Activity.this,Friend_Detail_Activity.class));
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
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, ChatRecordActivity.class));
    }
}