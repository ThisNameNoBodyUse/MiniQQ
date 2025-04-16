package Activity;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;
//聊天记录

public class ChatRecordActivity extends AppCompatActivity {
    private byte[] avatar;//好友头像
    private String str_friend_name;//好友名字
    private SharedPreferences sp;//读取好友qq
    private UserDbHelper db;
    private int friend_id;
    private int user_id;
    private int friend_qq;
    private int main_qq;

    //控件
    private ImageView btn_back;//返回
    private ImageView friend_avatar;//好友头像
    private TextView friend_name;//好友名字
    private Button delete_chat_record;//删除聊天记录
    private Button delete_friend;//删除好友


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_record);
        //加载控件
        btn_back = findViewById(R.id.btn_back);
        friend_avatar = findViewById(R.id.friend_avatar);
        friend_name = findViewById(R.id.friend_name);
        delete_chat_record = findViewById(R.id.delete_chat_record);
        delete_friend = findViewById(R.id.delete_friend);

        db = UserDbHelper.getInstance(this);

        sp = getSharedPreferences("friend_data", MODE_PRIVATE);

        friend_qq = sp.getInt("friend_qq", 0);

        sp = getSharedPreferences("user_data", MODE_PRIVATE);
        main_qq = sp.getInt("main_qq", 0);
        friend_id = db.getUser_id(friend_qq);
        Log.d("ChatRecordActivity", "friend_id is : " + friend_id);
        user_id = db.getUser_id(main_qq);
        Log.d("ChatRecordActivity", "user_id is : " + user_id);
        avatar = db.getAvatar(friend_qq);
        str_friend_name = db.getUsername(friend_qq);

        //加载头像
        Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        friend_avatar.setImageBitmap(bitmap);

        //加载名字
        friend_name.setText(str_friend_name);

        //按钮点击事件

        //返回
        btn_back.setOnClickListener(view -> {
            startActivity(new Intent(ChatRecordActivity.this, ChatActivity.class));
        });

        //删除聊天记录
        delete_chat_record.setOnClickListener(view -> {
            //危险**********
            delete_each_record();
        });

        //删除好友
        delete_friend.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRecordActivity.this);
            //设置对话框的标题和内容
            builder.setTitle("删除好友");
            builder.setMessage("您确定要删除"+str_friend_name+"吗?");
            //对话框的正面按钮,删除好友
            builder.setPositiveButton("是", (dialogInterface, i) -> {
                SQLiteDatabase db1 = UserDbHelper.getInstance(this).getReadableDatabase();
                //从friend表中删除两条记录,分别表示当前用户和该好友的关系
                db1.delete("friend", "user_id=? and friend_id=?", new String[]{String.valueOf(user_id), String.valueOf(friend_id)});
                db1.delete("friend", "user_id=? and friend_id=?", new String[]{String.valueOf(friend_id), String.valueOf(user_id)});
                //关闭数据库
                db1.close();
                delete_each_record();
                Toast.makeText(ChatRecordActivity.this, "删除成功!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ChatRecordActivity.this, MainActivity_Second.class));

            });
            //对话框反面按钮
            builder.setNegativeButton("否", (dialogInterface, i) -> {
                dialogInterface.cancel();//取消
            });
            builder.create().show();
        });
        delete_chat_record.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatRecordActivity.this);

            builder.setTitle("删除好友");
            builder.setMessage("您确定要清空和" + str_friend_name + "的聊天记录吗?");
            //对话框的正面按钮,清空
            builder.setPositiveButton("确定", (dialogInterface, i) -> {
                delete_each_record();
                Toast.makeText(ChatRecordActivity.this, "清空成功!", Toast.LENGTH_SHORT).show();
            });
            //对话框反面按钮,取消
            builder.setNegativeButton("取消", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
            builder.create().show();
        });
    }

    public void delete_each_record(){
        //这个很危险********
        //定义删除事件的逻辑并更新数据库
        SQLiteDatabase db = UserDbHelper.getInstance(this).getWritableDatabase();
        //从数据库中删除和好友的所有聊天记录
        db.delete("message_table", "sender_id = ? and receiver_id = ? or sender_id = ? and receiver_id = ?", new String[]{String.valueOf(user_id), String.valueOf(friend_id), String.valueOf(friend_id), String.valueOf(user_id)});
        //db.delete("message_table", "sender_id = ? and receiver_id = ?", new String[]{String.valueOf(user_id), String.valueOf(friend_id)});
        //关闭数据库对象
        db.close();
    }
    public void to_friend_info(View view){
        startActivity(new Intent(ChatRecordActivity.this,Friend_Info_Activity.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, ChatActivity.class));
    }
}

