package Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pretend_qq.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

import SQLite.UserDbHelper;

public class RegisterActivity_Second extends AppCompatActivity {
    private UserDbHelper db;
    private ImageView ig_avatar;
    private EditText et_username;
    private RadioGroup rg_gender; //性别单选组
    private Spinner sp_city;//城市
    private Spinner sp_year;//年
    private Spinner sp_month;//月
    private Spinner sp_day;//日
    private Spinner sp_question1;//密保问题1
    private EditText et_answer1;//密保1答案
    private Spinner sp_question2;//密保问题2
    private EditText et_answer2;//密保2答案
    private EditText et_password;//密码
    private EditText et_password_confirm;//确认密码
    private Button register;//注册按钮
    private ImageView img_back;//返回


    private int qq_num;//qq号,8位随机
    private byte[] avatar;
    private String gender = "";
    private String str_year="";
    private String str_month="";
    private String str_day="";
    private int year = 0;
    private int month = 0;
    private int day = 0;
    private String username="";//用户名
    private String city = "";
    private String question1 = "";
    private String answer1 = "";
    private String question2="";
    private String answer2="";
    private String password = "";
    private String password_confirm = "";
    private SharedPreferences sp;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_second);
        db=UserDbHelper.getInstance(this);
        ig_avatar = findViewById(R.id.ig_avatar);
        et_username = findViewById(R.id.et_username);
        rg_gender = findViewById(R.id.rg_gender);
        sp_city = findViewById(R.id.sp_city);
        sp_year = findViewById(R.id.sp_year);
        sp_month = findViewById(R.id.sp_month);
        sp_day = findViewById(R.id.sp_day);
        sp_question1 = findViewById(R.id.sp_question1);//密保问题1
        et_answer1 = findViewById(R.id.et_answer1);//密保1答案
        sp_question2=findViewById(R.id.sp_question2);//密保问题2
        et_answer2=findViewById(R.id.et_answer2);//密保2答案
        et_password = findViewById(R.id.et_password);
        et_password_confirm = findViewById(R.id.et_password_confirm);
        register = findViewById(R.id.btn_register);
        img_back=findViewById(R.id.img_back);
        //返回监听
        img_back.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity_Second.this,LoginActivity_Second.class));
        });

        //更换初始头像
        ig_avatar.setOnClickListener(view -> {
            //创建一个Intent对象,用于打开系统相册或相机
            Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //启动相册或相机,并等待返回结果
            startActivityForResult(intent1, 1);
        });

        //选择性别
        rg_gender.setOnCheckedChangeListener((group, checkId) -> {
            RadioButton rb = group.findViewById(checkId);
            if (rb != null) {
                gender=rb.getText().toString();
            }
        });
        //选择城市
        sp_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l){
                city = parent.getSelectedItem().toString();
                Log.d("RegisterActivity", "city is: " + city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){

            }
        });
        //选择年
        sp_year.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                str_year = parent.getSelectedItem().toString();
                year = Integer.parseInt(str_year);
                Log.d("RegisterActivity_Second", "year is: " + year);
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
                Log.d("RegisterActivity_Second", "month is: " + month);
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
        //监听密保问题1
        sp_question1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                question1=parent.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //监听密保问题2
        sp_question2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                question2=parent.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //注册按钮
        register.setOnClickListener(view -> {
            //用户名
            username=et_username.getText().toString();
            Boolean flag=false;//未标记
            Log.d("RegisterActivity_Second","username is: "+username);

            //密保1答案
            answer1=et_answer1.getText().toString();
            Log.d("RegisterActivity","answer1 is: "+answer1);

            //密保2答案
            answer2=et_answer2.getText().toString();

            //密码
            password=et_password.getText().toString();
            //确认密码
            password_confirm=et_password_confirm.getText().toString();

            if(username.equals("")){
                Toast.makeText(RegisterActivity_Second.this,"请设置您的用户名!",Toast.LENGTH_SHORT).show();
            }
            else if(gender.equals("")){
                Toast.makeText(RegisterActivity_Second.this,"请设置您的性别!",Toast.LENGTH_SHORT).show();
            }
            else if(city.equals("")){
                Toast.makeText(RegisterActivity_Second.this,"请设置您的城市!",Toast.LENGTH_SHORT).show();
            }
            else if(str_year.equals("")||str_month.equals("")||str_day.equals("")){
                Toast.makeText(RegisterActivity_Second.this,"请完善您的生日!",Toast.LENGTH_SHORT).show();
            }

            else if(answer1.equals("")||answer2.equals("")){
                Toast.makeText(RegisterActivity_Second.this,"请完善您的密保!",Toast.LENGTH_SHORT).show();
            }

            else if(password.equals("")){
                Toast.makeText(RegisterActivity_Second.this,"请设置您的密码!",Toast.LENGTH_SHORT).show();
            }
            else if(password_confirm.equals("")){
                Toast.makeText(RegisterActivity_Second.this,"请确认您的密码!",Toast.LENGTH_SHORT).show();
            }

            else if(!password.equals(password_confirm)){
                Toast.makeText(RegisterActivity_Second.this,"两次密码不一致!",Toast.LENGTH_SHORT).show();
            }
            else if(!username.equals("") &&!gender.equals("")&&!city.equals("")&&!str_year.equals("")&&!str_month.equals("")&&!str_day.equals("")&&!answer1.equals("")&&!answer2.equals("")&&!password.equals("")&&!password_confirm.equals("")&&password.equals(password_confirm)){
                if(password.length()<8)flag=true;
                if(flag)Toast.makeText(RegisterActivity_Second.this,"密码不能少于8位!",Toast.LENGTH_SHORT).show();
                else{
                    int row=db.register(qq_num,password);//注册,qq和密码
                    if(row>0){
                        AlertDialog.Builder builder=new AlertDialog.Builder(RegisterActivity_Second.this);
                        Toast.makeText(RegisterActivity_Second.this,"注册成功!",Toast.LENGTH_SHORT).show();
                        builder.setTitle("请确认您的QQ号");
                        qq_num=create_qq();
                        builder.setMessage("您的QQ号是:"+qq_num);
                        builder.setPositiveButton("确定", (dialogInterface, i) -> {
                            Log.d("RegisterActivity_Second","您的QQ号是: "+qq_num);
                            Intent intent=new Intent(RegisterActivity_Second.this,LoginActivity_Second.class);
                            intent.putExtra("user_qq",qq_num);
                            startActivity(intent);
                        });
                        builder.create().show();
                        //获取ImageView的图片源,转换为Bitmap对象
                        Bitmap bitmap = ((BitmapDrawable)ig_avatar.getDrawable()).getBitmap();
                        //创建一个字节数组输出流对象,用于存储压缩后的图片
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        //使用Bitmap类的compress方法,将图片压缩为JPEG格式,质量为100%,输出到字节数组输出流对象中
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        //从字节数组输出流对象中获取字节数组,赋值给avatar变量
                        avatar= baos.toByteArray();
                        //关闭字节数组输出流对象,释放资源
                        try{
                            baos.close();
                        } catch (IOException e){
                            throw new RuntimeException(e);
                        }
                        db.register(qq_num,password);//注册到数据库中
                        db.updateAvatar(qq_num,avatar);//存储头像
                        db.updateUsername(qq_num,username);//存储用户名
                        db.updateGender(qq_num,gender);//存储性别
                        db.updateRegion(qq_num,city);//存储城市
                        db.updateYear(qq_num,year);//存储年
                        db.updateMonth(qq_num,month);//存储月
                        db.updateDay(qq_num,day);//存储日
                        db.updateQuestion1(qq_num,question1);//存储密钥1
                        db.updateAnswer1(qq_num,answer1);
                        db.updateQuestion2(qq_num,question2);//存储密钥2
                        db.updateAnswer2(qq_num,answer2);
                        db.close();

                        UserDbHelper db1=UserDbHelper.getInstance(this);
                        //打印测试
                        String str_password=db1.getPassword(qq_num);
                        String str_question1=db1.getQuestion1(qq_num);
                        String str_answer1=db1.getAnswer1(qq_num);
                        String str_question2=db1.getQuestion2(qq_num);
                        String str_answer2=db1.getAnswer2(qq_num);
                        
                        Log.d("RegisterActivity_Second","str_password is: "+str_password);
                        Log.d("RegisterActivity_Second","str_question1 is: "+str_question1);
                        Log.d("RegisterActivity_Second","str_answer1 is "+str_answer1);
                        Log.d("RegisterActivity_Second","str_question2 is: "+str_question2);
                        Log.d("RegisterActivity_Second","str_answer2 is "+str_answer2);
                        db1.close();
                        flag=false;
                    }else{
                        Toast.makeText(RegisterActivity_Second.this,"注册失败!",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

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
    }
    public int create_qq(){
        //生成8-10位数qq
        Random r=new Random();
        int i=0;
        int num=0;
        while(i<8){
            int digit=r.nextInt(10);
            if(i==0&&digit==0){
                continue;
            }
            num+=num*10+digit;
            i++;
        }
        return num;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断请求码和结果码是否正确
        if (requestCode==1&&resultCode==RESULT_OK) {
            //获取返回的图片数据
            Uri uri=data.getData();
            try{
                //将图片数据转换为Bitmap对象
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                //将Bitmap对象设置为头像控件的图片源
                ig_avatar.setImageBitmap(bitmap);

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(RegisterActivity_Second.this, LoginActivity_Second.class));
    }

}