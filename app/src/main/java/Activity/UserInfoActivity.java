package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretend_qq.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import SQLite.UserDbHelper;
import Tools.Friend;
import Tools.Msg;

public class UserInfoActivity extends AppCompatActivity {
    //添加好友的界面
    private int main_qq;//主体qq
    private int find_qq;//查询qq
    private UserDbHelper db;//操作数据库
    private TextView tv_year;
    private TextView tv_month;
    private TextView tv_day;
    private TextView user_qq;
    private TextView tv_user_name;
    private TextView tv_user_motto;
    private TextView tv_user_gender;
    private TextView tv_user_city;
    private ImageView user_avatar;//头像
    private Button btn_add_friend;
    private Button cancel;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //获取传递过来的用户信息
        Intent intent = getIntent();

        main_qq=intent.getIntExtra("main_qq",0);
        find_qq = intent.getIntExtra("find_qq",0);

        db=UserDbHelper.getInstance(this);
        //获取本用户id
        int user_id=db.getUser_id(main_qq);
        int friend_id=db.getUser_id(find_qq);

        String username=db.getUsername(find_qq);
        String gender=db.getGender(find_qq);//性别
        String city=db.getRegion(find_qq);//城市
        int year=db.getYear(find_qq);
        int month=db.getMonth(find_qq);
        int day=db.getDay(find_qq);
        String signature=db.getSignature(find_qq);//座右铭
        byte[]avatar=db.getAvatar(find_qq);//头像
        //显示用户信息
        tv_user_name = findViewById(R.id.tv_user_name);//用户名
        tv_user_motto = findViewById(R.id.tv_user_motto);//头像
        tv_user_gender=findViewById(R.id.tv_user_gender);//性别
        tv_user_city=findViewById(R.id.tv_user_city);//城市
        tv_year=findViewById(R.id.tv_year);
        tv_month=findViewById(R.id.tv_month);
        tv_day=findViewById(R.id.tv_day);
        user_avatar=findViewById(R.id.user_avatar);//头像
        user_qq=findViewById(R.id.user_qq);


        //按钮信息
        cancel=findViewById(R.id.cancel);
        btn_add_friend=findViewById(R.id.add_friend);

        //给对应控件设置对应属性
        tv_user_name.setText(username);
        tv_user_motto.setText(signature);
        tv_user_gender.setText(gender);
        tv_user_city.setText(city);
        user_qq.setText(Integer.toString(find_qq));

        tv_year.setText(Integer.toString(year));
        tv_month.setText(Integer.toString(month));
        tv_day.setText(Integer.toString(day));

        //设置头像
        if(avatar!=null){
            Bitmap bitmap= BitmapFactory.decodeByteArray(avatar,0,avatar.length);
            user_avatar.setImageBitmap(bitmap);
        }else{
            user_avatar.setImageResource(R.drawable.qq);
        }

        //添加好友
        btn_add_friend.setOnClickListener(view -> {

            Friend friend=new Friend(friend_id,find_qq,username,avatar,"离线");
            //Friend me=new Friend(find_qq,friend_id,db.getUsername(main_qq),db.getAvatar(main_qq),"离线");
            Friend me=new Friend(user_id,main_qq,db.getUsername(main_qq),db.getAvatar(main_qq),"离线");
            
            Log.d("UserInfoActivity","user_id and friend_id is :"+user_id+", "+friend_id);
            db.add_friend(user_id,friend_id,friend,me);
            Toast.makeText(UserInfoActivity.this,"添加成功!",Toast.LENGTH_SHORT).show();
            Log.d("UserInfoActivity","FriendList is : "+db.getFriendList(user_id));

            // TODO: 将消息发送给好友，并保存到数据库中
            SQLiteDatabase db1 = UserDbHelper.getInstance(this).getWritableDatabase(); // 获取可写的数据库对象
            ContentValues values = new ContentValues(); // 创建一个键值对的对象，用于存放要插入的数据
            values.put("sender_id",friend_id);//将发送者的ID存入values 中
            values.put("receiver_id",user_id);//将接收者的ID存入values中
            values.put("content","我们已经是好友了,现在开始聊天吧!");//将消息的内容存入values中
            values.put("type", Msg.TYPE_SENT);//将消息的类型存入values中
            values.put("time", getNowTime()); // 将消息的时间存入values中
            db1.insert("message_table", null, values); // 将 values 中的数据插入到 message_table 中
            db1.close(); // 关闭数据库


            Intent intent1=new Intent(UserInfoActivity.this,MainActivity_Second.class);
            intent1.putExtra("main_qq",main_qq);
            startActivity(intent1);
        });

        cancel.setOnClickListener(view -> {
            Intent intent1=new Intent(UserInfoActivity.this,SearchActivity.class);
            intent1.putExtra("main_qq",main_qq);
            startActivity(intent1);
        });
    }
    public String getNowTime(){
        //获取当前时间
        Calendar calendar = Calendar.getInstance (); //创建一个Calendar对象
        long now = calendar.getTimeInMillis (); //获取当前的时间,以毫秒为单位
        SimpleDateFormat format = new SimpleDateFormat("HH:mm"); //创建一个格式化对象,指定小时和分钟
        Date date = new Date (now); //创建一个Date对象,表示当前的时间
        String time = format.format (date); //格式化当前的时间,返回一个字符串
        return time;
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        Intent intent1=new Intent(UserInfoActivity.this,SearchActivity.class);
        intent1.putExtra("main_qq",main_qq);
        startActivity(intent1);
    }
}
