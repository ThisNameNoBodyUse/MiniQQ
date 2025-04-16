package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.pretend_qq.R;

// 新建一个 ChatActivity.java，用于显示聊天界面


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Adapter.MsgAdapter;
import SQLite.UserDbHelper;
import Tools.Msg;

public class ChatActivity extends AppCompatActivity {
    private SharedPreferences sp;//获取主用户qq
    private SharedPreferences sp1;//获取好友qq
    private ImageView back;
    private UserDbHelper db;
    private List<Msg>msgList = new ArrayList<>(); // 存储消息的列表
    private TextView friend_name;//好友名字=>标题
    private String str_friend_name;//好友名字
    private ImageView select;//选项,进入删除/清空界面
    private EditText inputText;//输入框
    private Button send; //发送按钮
    private RecyclerView msgRecyclerView; //消息列表视图
    private MsgAdapter adapter; //消息适配器
    private int user_qq;//当前用户qq
    private int friend_qq;//好友qq

    private int user_id;
    private int friend_id;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        friend_name=findViewById(R.id.friend_name);
        select=findViewById(R.id.select);

        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        sp1=getSharedPreferences("friend_data",MODE_PRIVATE);
        // 获取传递过来的用户qq和好友qq
        user_qq=sp.getInt("main_qq",0);
        friend_qq=sp1.getInt("friend_qq",0);

        str_friend_name=db.getUsername(friend_qq);
        friend_name.setText(str_friend_name);

        Log.d("ChatActivity","user_qq is : "+user_qq);
        Log.d("ChatActivity","friend_qq is : "+friend_qq);

        UserDbHelper db=UserDbHelper.getInstance(this);

        user_id=db.getUser_id(user_qq);

        Log.d("ChatActivity","user_id:"+user_id);
        friend_id=db.getUser_id(friend_qq);
        db.close();

        //初始化消息数据
        initMsgs();

        inputText = findViewById(R.id.input_text);//输入框

        //删除/清空界面
        select.setOnClickListener(view -> {
            startActivity(new Intent(ChatActivity.this,ChatRecordActivity.class));
        });

        back=findViewById(R.id.btn_back);//返回,先设置主界面
        back.setOnClickListener(view -> {
             startActivity(new Intent(ChatActivity.this,MainActivity_Second.class));
        });

        send =findViewById(R.id.send);
        msgRecyclerView = findViewById(R.id.msg_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        msgRecyclerView.setLayoutManager(layoutManager);

        byte[]user_avatar=db.getAvatar(user_qq);
        byte[]friend_avatar=db.getAvatar(friend_qq);

        //初始化适配器和消息列表
        adapter = new MsgAdapter(msgList,user_avatar,friend_avatar);
        msgRecyclerView.setAdapter(adapter);

        send.setOnClickListener(v ->{
            String content = inputText.getText().toString();//发送的内容
            String time=getNowTime();//发送时间

            if(!"".equals(content)){
                Msg msg = new Msg(content, Msg.TYPE_SENT,time); //创建一条发送的消息

                //TODO: 聊天界面的显示
                msgList.add(msg); //添加到消息列表
                adapter.notifyItemInserted(msgList.size()-1); //通知适配器有新消息插入
                msgRecyclerView.scrollToPosition(msgList.size()-1); //将消息列表滚动到最后一条
                inputText.setText(""); //清空输入框

                // TODO: 将消息发送给好友,并保存到数据库中
                SQLiteDatabase db1=db.getWritableDatabase(); //获取可写的数据库对象
                ContentValues values = new ContentValues(); //创建一个键值对的对象,用于存放要插入的数据
                values.put("sender_id",user_id);//将发送者的ID存入values 中,user_id是当前登录的用户的ID
                values.put("receiver_id",friend_id);//将接收者的ID存入values中,friend_id是你当前聊天的好友的ID
                values.put("content",content);//将消息的内容存入values中
                values.put("type", Msg.TYPE_SENT);//将消息的类型存入values中
                values.put("time", time); //将消息的时间存入values中
                db1.insert("message_table", null, values); //将values中的数据插入到message_table中
                db1.close(); // 关闭数据库

            }else{
                Toast.makeText(ChatActivity.this,"发送消息不能为空!",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initMsgs() {
        // TODO: 从数据库中读取和好友的聊天记录,并添加到 msgList中
        SQLiteDatabase db1 = db.getReadableDatabase(); //获取可读的数据库对象
        Cursor cursor = db1.query("message_table", null, "sender_id = ? and receiver_id = ? or sender_id = ? and receiver_id = ?", new String[]{String.valueOf(user_id), String.valueOf(friend_id), String.valueOf(friend_id), String.valueOf(user_id)}, null, null, "_id asc"); //查询 message_table中和当前好友相关的所有消息,按照 _id升序排列
        if (cursor.moveToFirst()) {
            //如果有数据,将游标移动到第一条记录
            do{
                @SuppressLint("Range")
                String content = cursor.getString(cursor.getColumnIndex("content")); //获取消息的内容
                @SuppressLint("Range") String time=cursor.getString(cursor.getColumnIndex("time"));//消息的时间
                @SuppressLint("Range") int sender_id = cursor.getInt(cursor.getColumnIndex("sender_id")); //获取消息的发送者的ID
                Log.d("ChatActivity","sender_id:"+sender_id);
                Msg msg;
                if(user_id==sender_id){
                    //当前用户是发送者
                    msg = new Msg(content,Msg.TYPE_SENT,time);
                }else{
                    //当前用户是接收者
                    msg = new Msg(content,Msg.TYPE_RECEIVED,time);
                }
                msgList.add(msg); //将Msg对象添加到 msgList中
            }while(cursor.moveToNext()); //如果有下一条记录,将游标移动到下一条记录
        }
        cursor.close(); //关闭游标
        db1.close(); //关闭数据库
    }
    public String getNowTime(){
        //获取当前时间
        Calendar calendar = Calendar.getInstance (); //创建一个Calendar对象
        long now = calendar.getTimeInMillis (); //获取当前的时间,以毫秒为单位
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        Date date = new Date (now); //Date对象,表示当前的时间
        String time = format.format (date); //格式化当前的时间
        return time;
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, MainActivity_Second.class));
    }
}
