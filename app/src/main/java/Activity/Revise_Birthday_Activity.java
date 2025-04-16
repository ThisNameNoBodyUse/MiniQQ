package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import java.time.LocalDate;
import java.time.Period;

import SQLite.UserDbHelper;

public class Revise_Birthday_Activity extends AppCompatActivity {
    //选择出生日期
    private ImageView img_back;
    private UserDbHelper db;
    private SharedPreferences sp;
    private Button save_birthday;
    private Spinner sp_year;
    private Spinner sp_month;
    private Spinner sp_day;
    private TextView tv_age;//设置年龄的控件
    private int main_qq;
    private String str_year;//选的年
    private String str_month;//选的月
    private String str_day;//选的日
    private int year;
    private int month;
    private int day;
    private int age;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_birthday);
        //初始化该用的控件
        sp_year=findViewById(R.id.sp_year);
        sp_month=findViewById(R.id.sp_month);
        sp_day=findViewById(R.id.sp_day);
        tv_age=findViewById(R.id.tv_age);
        save_birthday=findViewById(R.id.save_birthday);
        img_back=findViewById(R.id.img_back);
        //返回按钮
        img_back.setOnClickListener(view -> {
            startActivity(new Intent(Revise_Birthday_Activity.this,Revise_Message_Activity.class));
        });
        //保存按钮
        save_birthday.setOnClickListener(view -> {
            db.updateYear(main_qq,year);
            db.updateMonth(main_qq,month);
            db.updateDay(main_qq,day);
            Toast.makeText(this, "保存成功!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Revise_Birthday_Activity.this, Revise_Message_Activity.class));
        });

        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);

        year=db.getYear(main_qq);
        month=db.getMonth(main_qq);
        day=db.getDay(main_qq);
        Log.d("Revise_Birthday","day is : "+day);
        age=getAge(year,month,day);
        refreshDaySpinner(year,month);

        //设置Spinner控件的选中项
        sp_year.setSelection(year-2000);
        sp_month.setSelection(month-1); //Spinner控件的适配器是从1月开始的
        sp_day.setSelection(day-1); //Spinner控件的适配器是从1日开始的
        //设置TextView控件的文本
        tv_age.setText(String.valueOf(age)); // 将年龄转换为字符串
        //选择年
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                str_year = parent.getSelectedItem().toString();
                year = Integer.parseInt(str_year);
                refreshDaySpinner(year, month);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //选择月
        sp_month.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                str_month = parent.getSelectedItem().toString();
                month = Integer.parseInt(str_month);
                refreshDaySpinner(year, month);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //选择日
        sp_day.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                str_day=parent.getSelectedItem().toString();
                day=Integer.parseInt(str_day);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
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
    //定义一个方法来根据年和月刷新日的控件
    public void refreshDaySpinner(int year, int month) {
        //获取日的控件的引用
        Spinner sp_day = (Spinner) findViewById(R.id.sp_day);
        //定义一个字符串数组来存储日期的条目内容
        String[] day_array;
        //根据月份的值，选择不同的日期范围
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                //如果月份是1,3,5,7,8,10,12
                day_array = getResources().getStringArray(R.array.day_array); //使用1到31天的数组
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                //如果月份是4,6,9,11
                day_array = getResources().getStringArray(R.array.day_array_1); //使用1到30天的数组
                break;
            case 2:
                //如果月份是2
                if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) { //如果年份是闰年
                    day_array = getResources().getStringArray(R.array.day_array_2); //使用1到29天的数组
                } else {
                    //如果年份是平年
                    day_array = getResources().getStringArray(R.array.day_array_3); //使用1到28天的数组
                }
                break;
            default:
                //如果月份是其他值
                day_array = null;
                //使用空数组
                break;
        }
        //检查day_array是否为null
        if (day_array != null) {
            //创建一个适配器
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, day_array);
            //设置下拉视图的样式
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //将适配器绑定到日的控件
            sp_day.setAdapter(adapter);
            //通知适配器数据已改变
            adapter.notifyDataSetChanged();
        }
        //设置日的控件的选中项，根据你之前选择的日期
        if (day - 1 < day_array.length) { //检查day-1是否小于day_array的长度
            sp_day.setSelection(day - 1); //使用day-1而不是day
        } else {
            sp_day.setSelection(day_array.length - 1); //如果day-1大于等于day_array的长度,使用day_array的最后一个元素的索引
            day=Integer.parseInt(sp_day.getSelectedItem().toString());
        }

        //重新计算年龄，并且更新TextView控件的文本
        age = getAge(year, month, day); //调用getAge方法
        tv_age.setText(String.valueOf(age)); //将年龄转换为字符串
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, Revise_Message_Activity.class));
    }

}