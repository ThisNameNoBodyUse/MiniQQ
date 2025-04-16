package Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pretend_qq.R;

import java.util.List;

import SQLite.UserDbHelper;
import Tools.Comment;

//动态评论部分的适配器
//定义一个CommentAdapter类,继承自RecyclerView.Adapter,用于为RecyclerView提供评论的视图和数据
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    //定义一个存储评论的集合
    private List<Comment> commentList;
    //定义一个上下文对象
    private Context context;
    //定义一个UserDbHelper对象,用于操作数据库
    private UserDbHelper db;

    //定义一个带参数的构造方法,用于创建CommentAdapter对象
    public CommentAdapter(Context context, List<Comment> commentList) {
        this.context = context;
        this.commentList = commentList;
        //获取UserDbHelper的实例
        db = UserDbHelper.getInstance(context);
    }

    //重写onCreateViewHolder方法,返回一个ViewHolder对象,用于创建评论的视图
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //使用LayoutInflater来加载评论的布局文件
        View view = LayoutInflater.from(context).inflate(R.layout.layout_comment_item, parent, false);
        //创建ViewHolder对象
        ViewHolder holder = new ViewHolder(view);
        //返回ViewHolder对象
        return holder;
    }

    //重写onBindViewHolder方法,用于绑定评论的数据到视图上
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //获取指定位置的评论对象
        Comment comment = commentList.get(position);
        //获取评论的用户的ID
        int user_id = comment.getUser_id();
        Log.d("CommentAdapter","user_id is : "+user_id);
        //获取评论的用户的昵称

        String name = db.getUsername(user_id);
        if (name.equals("")|| name == null) {
            name="账号已注销";
        }
        Log.d("CommentAdapter","name is : "+name);

        //获取评论的内容
        String content = comment.getContent();
        Log.d("CommentAdapter","content is : "+content);
        //设置昵称和内容给TextView
        holder.nameTextView.setText(name + ":           ");
        holder.contentTextView.setText(content);
    }

    //重写getItemCount方法，返回评论的数量
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    //定义一个ViewHolder类,继承自RecyclerView.ViewHolder,用于缓存视图中的控件
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView; //昵称
        TextView contentTextView; //内容

        //定义一个带参数的构造方法,用于创建ViewHolder对象
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //将视图中的控件绑定到ViewHolder对象中
            nameTextView = itemView.findViewById(R.id.comment_sayer);
            contentTextView = itemView.findViewById(R.id.comment_text_view);
        }
    }
}

