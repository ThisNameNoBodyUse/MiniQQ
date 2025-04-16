package Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.example.pretend_qq.R;

import java.util.ArrayList;
import java.util.List;

import Activity.ChatActivity;
import Adapter.MessageAdapter;
import SQLite.UserDbHelper;
import Tools.Message;

public class MessageFragment extends Fragment {
    //主界面中的消息Fragment
    private UserDbHelper db1;
    private int main_qq;
    private SharedPreferences sp;//读取主qq
    private SharedPreferences sp1;//存储好友qq
    private SharedPreferences.Editor editor;
    private int user_id;
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private List<Message> messageList;
    @SuppressLint("Range")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_message, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        messageList = new ArrayList<>();

        db1=UserDbHelper.getInstance(getContext());
        sp= getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        user_id=db1.getUser_id(main_qq);

        // TODO: 从数据库中读取和好友的聊天记录，并添加到 messageList 中
        // 获取可读的数据库对象
        SQLiteDatabase db=UserDbHelper.getInstance(getContext()).getReadableDatabase();
        // 获取自己的好友列表
        List<Integer> friendList = new ArrayList<>();
        Cursor cursor1 = db.query("friend_table", new String[]{"friend_id"}, "user_id = ?", new String[]{String.valueOf(user_id)}, null, null, null);
        if (cursor1.moveToFirst()) {
            do {
                // 获取好友的ID
                @SuppressLint("Range") int friend_id = cursor1.getInt(cursor1.getColumnIndex("friend_id"));
                // 将好友的ID添加到好友列表中
                friendList.add(friend_id);
            } while (cursor1.moveToNext());
        }
        cursor1.close();
        //遍历好友列表,查询有哪些好友和自己有过聊天记录
        for (int friend_id : friendList) {
            //查询最后一条聊天记录的相关信息
            Cursor cursor2 = db.query("message_table as m inner join user_table as u on m.sender_id = u._id", new String[]{"m._id", "u.avatar", "u.username", "m.content", "m.time"}, "m.sender_id = ? and m.receiver_id = ? or m.sender_id = ? and m.receiver_id = ?", new String[]{String.valueOf(user_id), String.valueOf(friend_id), String.valueOf(friend_id), String.valueOf(user_id)}, null, null, "m._id desc limit 1");
            if (cursor2.moveToFirst()) {
                //如果有聊天记录,获取相关信息

                Cursor cursor3 = db.query("user_table", new String[]{"avatar", "username"}, "_id = ?", new String[]{String.valueOf(friend_id)}, null, null, null);
                byte[]avatar = null;
                String name = null;
                if (cursor3.moveToFirst()) {
                    // 如果查询到结果,就获取接收者的头像和名字
                     avatar = cursor3.getBlob(cursor3.getColumnIndex("avatar")); //好友的的头像
                     name = cursor3.getString(cursor3.getColumnIndex("username")); //好友的名字
                }
                @SuppressLint("Range") String content = cursor2.getString(cursor2.getColumnIndex("content"));//获取最后一条消息的内容
                @SuppressLint("Range") String time = cursor2.getString(cursor2.getColumnIndex("time")); //获取最近的消息的时间
                //创建一个Message对象,用于显示在消息列表界面
                Message message = new Message(avatar, name, content, time,friend_id);
                //将Message对象添加到messageList中
                messageList.add(message);
            }
            cursor2.close();
        }
        db.close();

        //创建一个MessageAdapter对象,并传入一个监听器对象
        adapter = new MessageAdapter(messageList, message -> {
            //定义点击事件的逻辑，例如跳转到对应的聊天界面
            //获取好友的ID
            int friend_id = message.getFriend_id();
            int friend_qq=0;
            SQLiteDatabase db3=UserDbHelper.getInstance(getContext()).getReadableDatabase();
            Cursor cursor;
            cursor=db3.query("friend_table",new String[]{"QQNum"},"friend_id=?",new String[]{String.valueOf(friend_id)},null,null,null);
            if(cursor.moveToNext()){
                friend_qq=cursor.getInt(cursor.getColumnIndex("QQNum"));
            }
            Log.d("MessageFragment","friend_qq in mef is : "+friend_qq);
            cursor.close();
            db3.close();
            sp1=getActivity().getSharedPreferences("friend_data",Context.MODE_PRIVATE);
            editor=sp1.edit();
            editor.putInt("friend_qq",friend_qq);
            editor.commit();
            // 启动聊天界面
            startActivity(new Intent(getActivity(), ChatActivity.class));
        });

        //设置消息列表界面的布局和适配器
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext())); // 设置布局管理器为线性布局
        recyclerView.setAdapter(adapter); // 设置适配器为MessageAdapter
        return view;
    }
}
