package Adapter;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pretend_qq.R;

import java.util.List;

import Tools.Message;
import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messageList;

    //定义一个接口,用于监听点击事件
    public interface OnItemClickListener {
        void onItemClick(Message message); //定义一个方法,传入一个Message对象
    }

    //定义一个变量,用于存储监听器对象
    private OnItemClickListener listener;

    //定义一个构造方法,传入一个监听器对象
    public MessageAdapter(List<Message> messageList, OnItemClickListener listener) {
        Log.d("MessageAdapter","messageList is :"+messageList);
        this.messageList = messageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!messageList.isEmpty()){
            Message message = messageList.get(position);
            holder.bind(message, listener); //调用bind方法,传入Message对象和监听器对象
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView item_avatar;
        private TextView item_name;
        private TextView item_content;
        private TextView item_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_avatar = itemView.findViewById(R.id.item_avatar);
            item_name = itemView.findViewById(R.id.item_name);
            item_content = itemView.findViewById(R.id.item_content);
            item_time = itemView.findViewById(R.id.item_time);
        }

        //定义一个bind方法,用于绑定数据和监听点击事件
        public void bind(final Message message, final OnItemClickListener listener) {

            if (message == null || message.getAvatar() == null) {
                return; //或者给出一个默认的bitmap或者提示信息
            }
            Bitmap bitmap= BitmapFactory.decodeByteArray(message.getAvatar(),0,message.getAvatar().length);

            item_avatar.setImageBitmap(bitmap);
            item_name.setText(message.getName());
            item_content.setText(message.getContent());
            item_time.setText(message.getTime());
            //给itemView设置点击监听器
            itemView.setOnClickListener(v -> {
                // 调用监听器的onItemClick方法,传入Message对象
                listener.onItemClick(message);
            });
        }
    }
}
