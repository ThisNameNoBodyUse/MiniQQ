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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class Revise_Message_Activity extends AppCompatActivity {
    //这是编辑资料界面
    private ImageView user_avatar;
    private UserDbHelper db;
    private SharedPreferences sp;
    private int main_qq;
    private RadioGroup genderGroup; //性别的单选组
    private RadioButton male; //男
    private RadioButton female; //女

    private ImageView img_back;
    private TextView tv_signature;
    private TextView tv_username;
    private TextView tv_birthday;
    private Spinner sp_city;//城市下拉框
    private String str_signature;
    private String username;
    private String gender;
    private String birthday;
    private String city;
    private byte[]avatar;
    private int year;
    private int month;
    private int day;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_message);
        //初始化控件
        genderGroup = findViewById(R.id.gender_group);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);

        tv_birthday=findViewById(R.id.tv_birthday);
        tv_username=findViewById(R.id.tv_username);
        tv_signature=findViewById(R.id.tv_signature);
        sp_city=findViewById(R.id.sp_city);
        img_back=findViewById(R.id.img_back);
        user_avatar=findViewById(R.id.user_avatar);


        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        //获取信息
        avatar=db.getAvatar(main_qq);
        str_signature=db.getSignature(main_qq);
        username=db.getUsername(main_qq);
        gender=db.getGender(main_qq);
        year=db.getYear(main_qq);
        month=db.getMonth(main_qq);
        day=db.getDay(main_qq);
        birthday= year +"-"+ month +"-"+ day;
        city=db.getRegion(main_qq);
        //设置控件
        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
        user_avatar.setImageBitmap(bitmap);
        tv_username.setText(username);
        tv_birthday.setText(birthday);
        tv_signature.setText(str_signature);

        //获取字符串数组的引用
        String[] city_array = getResources().getStringArray(R.array.city_array);
        //设置Spinner控件的选中项，根据读取到的城市的值
        sp_city.setSelection(getIndex(city_array, city)); //调用getIndex方法,传入字符串数组和城市的值返回城市在数组中的索引

        //选择城市
        sp_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                city = parent.getSelectedItem().toString();
                db.updateRegion(main_qq,city);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        if(gender.equals("男")){
            male.setChecked(true);
            female.setChecked(false);
        }else{
            female.setChecked(true);
            male.setChecked(false);
        }

        //设置性别的单选组的选择事件,获取选择的性别
        genderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.male) {
                gender = "男";
            } else if (checkedId == R.id.female) {
                gender = "女";
            }
            db.updateGender(main_qq,gender);//更新性别
        });

        img_back.setOnClickListener(view -> {
            startActivity(new Intent(Revise_Message_Activity.this,Main_Info.class));
        });
    }
    public void go_to_revise_avatar(View view){
        startActivity(new Intent(Revise_Message_Activity.this,Revise_Avatar_Activity.class));
    }
    public void go_to_revise_signature(View view){
        startActivity(new Intent(Revise_Message_Activity.this,Revise_Signature_Activity.class));
    }
    public void go_to_revise_birthday(View view){
        startActivity(new Intent(Revise_Message_Activity.this, Revise_Birthday_Activity.class));
    }

    //定义一个getIndex方法,来根据字符串数组和城市的值,返回城市在数组中的索引
    private int getIndex(String[] array, String value) {
        int index = 0; //初始化索引为0
        for (int i = 0; i < array.length; i++) { //遍历字符串数组
            if (array[i].equals(value)) { //如果数组中的元素和城市的值相等
                index = i; //更新索引为当前的下标
                break; //跳出循环
            }
        }
        return index; //返回索引
    }
    public void go_to_revise_username(View view){
        startActivity(new Intent(Revise_Message_Activity.this, Revise_username_Activity.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, Main_Info.class));
    }

}