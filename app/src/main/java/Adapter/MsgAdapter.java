package Adapter;

// MsgAdapter.java


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.pretend_qq.R;

import java.util.List;

import Tools.Msg;

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<Msg>mMsgList; //存储消息的列表
    private byte[]user_avatar;
    private byte[]friend_avatar;
    static class ViewHolder extends RecyclerView.ViewHolder {


        LinearLayout leftLayout;
        LinearLayout rightLayout;
        TextView leftMsg; //左边的文本框
        TextView rightMsg; //右边的文本框
        ImageView left_avatar;
        ImageView right_avatar;
        TextView left_time;//左侧时间
        TextView right_time;//右侧时间

        public ViewHolder(View view) {
            super(view);
            leftLayout= view.findViewById(R.id.left_layout);
            rightLayout= view.findViewById(R.id.right_layout);
            leftMsg= view.findViewById(R.id.left_msg);
            rightMsg= view.findViewById(R.id.right_msg);
            left_avatar= view.findViewById(R.id.left_avatar);
            right_avatar= view.findViewById(R.id.right_avatar);
            left_time=view.findViewById(R.id.left_time);
            right_time=view.findViewById(R.id.right_time);
        }

    }

    public MsgAdapter(List<Msg> msgList, byte[]user_avatar, byte[]friend_avatar) {
        mMsgList = msgList;
        this.user_avatar=user_avatar;
        this.friend_avatar=friend_avatar;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //创建一个ViewHolder实例,并根据viewType参数加载不同的布局文件
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_item ,parent, false);
        return new ViewHolder(view); //传入viewType参数
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        //根据位置获取对应的消息对象
        Msg msg = mMsgList.get(position);
        //判断消息的类型,显示或隐藏不同的文本框
        if(msg.getType()==Msg.TYPE_RECEIVED){

            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            Bitmap bitmap= BitmapFactory.decodeByteArray(friend_avatar,0,friend_avatar.length);
            holder.left_avatar.setImageBitmap(bitmap);//左侧头像
            holder.left_time.setText(msg.getTime());//左侧时间
            holder.leftMsg.setText(msg.getContent());//左侧信息
        }else if(msg.getType() == Msg.TYPE_SENT){

            holder.rightLayout.setVisibility(View.VISIBLE);
            holder.leftLayout.setVisibility(View.GONE);
            Bitmap bitmap=BitmapFactory.decodeByteArray(user_avatar,0,user_avatar.length);
            holder.right_avatar.setImageBitmap(bitmap);//右侧头像
            holder.rightMsg.setText(msg.getContent());//右侧信息
            holder.right_time.setText(msg.getTime());//右侧时间
        }
    }

    @Override
    public int getItemCount() {
        //返回消息的数量
        return mMsgList.size();
    }
}

