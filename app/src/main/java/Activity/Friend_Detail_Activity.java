package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pretend_qq.R;

import java.time.LocalDate;
import java.time.Period;

import SQLite.UserDbHelper;

public class Friend_Detail_Activity extends AppCompatActivity {
    //好友信息详细界面
    private UserDbHelper db;
    private SharedPreferences sp;
    private int friend_qq;
    private String friend_name;
    private int age;
    private int month;
    private int year;
    private int day;
    private String gender;
    private String city;
    //控件
    private TextView tv_friend_username;
    private TextView tv_friend_gender;
    private TextView tv_friend_age;
    private TextView tv_friend_month;
    private TextView tv_friend_day;
    private TextView tv_friend_city;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_detail);
        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("friend_data",MODE_PRIVATE);
        friend_qq=sp.getInt("friend_qq",0);

        friend_name=db.getUsername(friend_qq);
        year=db.getYear(friend_qq);
        month=db.getMonth(friend_qq);
        day=db.getDay(friend_qq);
        age=getAge(year,month,day);
        gender=db.getGender(friend_qq);
        city=db.getRegion(friend_qq);
        //初始化控件
        tv_friend_username=findViewById(R.id.tv_friend_username);
        tv_friend_age=findViewById(R.id.tv_friend_age);
        tv_friend_month=findViewById(R.id.tv_friend_month);
        tv_friend_day=findViewById(R.id.tv_friend_day);
        tv_friend_city=findViewById(R.id.tv_friend_city);
        tv_friend_gender=findViewById(R.id.tv_friend_gender);
        //设置控件
        tv_friend_username.setText(friend_name);
        tv_friend_gender.setText(gender);
        tv_friend_month.setText(String.valueOf(month));
        tv_friend_age.setText(String.valueOf(age));
        tv_friend_day.setText(String.valueOf(day));
        tv_friend_city.setText(city);

    }
    public void go_to_friend_info(View view){
        startActivity(new Intent(Friend_Detail_Activity.this, Friend_Info_Activity.class));
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
        startActivity(new Intent(this, Friend_Info_Activity.class));
    }
}