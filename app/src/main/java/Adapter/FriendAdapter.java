package Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pretend_qq.R;

import java.util.List;

import Activity.ChatActivity;
import Activity.MainActivity_Second;
import SQLite.UserDbHelper;
import Tools.Friend;

//联系人的适配器类
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private UserDbHelper db; //操作数据库
    private List<Friend> friendList; //联系人列表
    private Context context; //上下文对象

    public FriendAdapter(int user_id, Context context) {
        //这里的user_id是在friend表中获得的
        db = UserDbHelper.getInstance(context);
        this.friendList = db.getFriendList(user_id);
        Log.d("FriendAdapter","friendList is : "+friendList);
        Log.d("FriendAdapter","user_id is : "+user_id);
        this.context = context;
    }

    // 创建ViewHolder类，用于缓存视图
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView; //卡片视图
        ImageView avatarView; //头像
        TextView nameView; //姓名
        TextView statusView; //在线状态


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            avatarView = itemView.findViewById(R.id.avatar_view);
            nameView = itemView.findViewById(R.id.name_view);
            statusView = itemView.findViewById(R.id.status_view);
        }
    }

    // 创建视图
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    // 绑定数据
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friend friend = friendList.get(position); //获取当前位置的联系人对象
        int friend_qq=friend.getQQNum();
        byte[]avatar=db.getAvatar(friend_qq);
        String friend_name=db.getUsername(friend_qq);
        Bitmap bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        holder.avatarView.setImageBitmap(bitmap); //设置头像
        holder.nameView.setText(friend_name); //设置姓名
        holder.statusView.setText(friend.getStatus()); //设置在线状态

        // 为卡片视图添加点击事件,跳转到聊天界面
        holder.cardView.setOnClickListener(v -> {
            sp= context.getSharedPreferences("friend_data", Context.MODE_PRIVATE);
            editor=sp.edit();
            editor.putInt("friend_qq",friend.getQQNum());
            Log.d("FriendAdapter","friend qq is : "+friend.getQQNum());//发现这里出错
            editor.commit();
            //跳转到聊天界面
            context.startActivity(new Intent(context,ChatActivity.class));
        });
    }

    // 获取列表长度
    @Override
    public int getItemCount() {
        return friendList.size();
    }

    //定义一个更新数据的方法
    public void update(List<Friend>friends) {
        //清空原来的数据
        friendList.clear();
        //添加新的数据
        friendList.addAll(friends);
        //通知适配器更新视图
        notifyDataSetChanged();
    }
}
