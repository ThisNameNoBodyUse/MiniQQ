package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pretend_qq.R;

import java.util.ArrayList;
import java.util.List;

import SQLite.UserDbHelper;
import Tools.Comment;
import Tools.Dongtai;

public class DongtaiAdapter extends RecyclerView.Adapter<DongtaiAdapter.ViewHolder> {
    private int user_like_count;
    private UserDbHelper db;
    private String sentences;
    private int sayer_qq;//评论者qq
    private String sayer_name;//评论者名字

    private List<Dongtai> dongtaiList;
    private Context context;

    //在DongtaiAdapter中定义一个存储评论的集合
    private List<Comment>commentList;
    //在DongtaiAdapter中定义一个为RecyclerView提供评论的视图和数据的适配器
    private CommentAdapter commentAdapter;
    //在DongtaiAdapter中定义一个为RecyclerView设置布局方式的对象
    private LinearLayoutManager layoutManager;

    public DongtaiAdapter(List<Dongtai> dongtaiList, Context context,int qq) {
        db=UserDbHelper.getInstance(context);
        this.dongtaiList = dongtaiList;
        this.context = context;
        sayer_qq=qq;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_dongtai, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Dongtai dongtai = dongtaiList.get(position);
        //发表说说的人的名字
        holder.nameTextView.setText(dongtai.getName());
        //说说的文字内容
        holder.contentTextView.setText(dongtai.getContent());
        //发表的图片
        if(dongtai.getPicture()!=null){
            Bitmap bitmap1=BitmapFactory.decodeByteArray(dongtai.getPicture(),0,dongtai.getPicture().length);
            Log.d("DongtaiActivity","picture bitmap1***** is : "+bitmap1);//非空
            holder.pictureImageView.setImageBitmap(bitmap1);
            holder.pictureImageView.setVisibility(View.VISIBLE);//设置可见
        }else{
            holder.pictureImageView.setVisibility(View.INVISIBLE);
        }


        //发表者的头像
        Bitmap bitmap= BitmapFactory.decodeByteArray(dongtai.getAvatar(),0,dongtai.getAvatar().length);
        holder.avatarImageView.setImageBitmap(bitmap);
        //总共点赞次数
        holder.likeTextView.setText(dongtai.getLike() + "赞");

        //进入页面初始每一部分是否已经点过赞,设置对应图标
        boolean is_liked=dongtai.isLiked();
        if(is_liked)holder.likeImageView.setImageResource(R.drawable.like_selected);
        else holder.likeImageView.setImageResource(R.drawable.like_normal);

        //创建一个commentList集合,用于存储评论的数据
        commentList = db.getCommentsByDongtaiId(dongtai.getId());
        //创建一个CommentAdapter对象,用于为RecyclerView提供评论的视图和数据
        commentAdapter = new CommentAdapter(context, commentList);

        //创建一个LinearLayoutManager对象,用于设置RecyclerView的布局方式为线性布局
        layoutManager = new LinearLayoutManager(context);

        //为评论列表的 RecyclerView设置适配器和布局管理器
        holder.commentRecyclerView.setAdapter(commentAdapter); // 为 RecyclerView 设置适配器
        holder.commentRecyclerView.setLayoutManager(new LinearLayoutManager(context));//为 RecyclerView 设置布局管理器

        //点赞部分的点击事件及数据库实现
        holder.likeImageView.setOnClickListener(v -> {
            //这里可以添加点赞的功能,或者更新数据库
            if (dongtai.isLiked()) { //如果已经点赞,就取消点赞
                user_like_count=db.getLikeCount(dongtai.getId());//获取点赞数量

                Log.d("DongtaiAdapter","dongtai_id1 is :"+dongtai.getId());
                Log.d("DongtaiAdapter","user_like_count is1 :"+user_like_count);

                user_like_count=user_like_count-1;
                dongtai.setLike(dongtai.getLike()-1); //点赞人数减一
                dongtai.set_liked(false); //设置点赞状态为false
                holder.likeImageView.setImageResource(R.drawable.like_normal); //设置点赞图标为R.mipmap.like_normal
                db.updateLikeCount(dongtai.getId(),user_like_count);
                db.updateIsLike(dongtai.getId(),0);

                Toast.makeText(context, "您取消了点赞", Toast.LENGTH_SHORT).show();

            } else { //如果没有点赞,就点赞
                user_like_count=db.getLikeCount(dongtai.getId());//获取点赞数量

                Log.d("DongtaiAdapter","dongtai_id2 is :"+dongtai.getId());
                Log.d("DongtaiAdapter","user_like_count is2 :"+user_like_count);

                user_like_count=user_like_count+1;
                dongtai.setLike(dongtai.getLike()+1); //点赞人数加一
                dongtai.set_liked(true); //设置点赞状态为true
                holder.likeImageView.setImageResource(R.drawable.like_selected); //设置点赞图标为R.mipmap.like_selected

                db.updateLikeCount(dongtai.getId(),user_like_count);
                db.updateIsLike(dongtai.getId(),1);

                Toast.makeText(context, "点赞成功", Toast.LENGTH_SHORT).show();
            }
            holder.likeTextView.setText(dongtai.getLike() + "赞"); //更新点赞人数的文本
        });
        //评论部分的评论事件及数据库实现
        holder.write_image_view.setOnClickListener(view -> {
            sentences=holder.edit_text.getText().toString();
            Log.d("DongtaiAdapter","Id* is : "+dongtai.getId());//确认id不同,代表不同的动态
            if(!sentences.equals("")){
                sayer_name=db.getUsername(sayer_qq);
                //创建一个Comment对象,用于封装评论的信息
                Comment comment = new Comment(-1, dongtai.getId(), sayer_qq, sentences);
                //判断当前的动态id是否和评论的动态id相同
                if (comment.getDongtai_id() == dongtai.getId()) {
                    //调用数据库的insertComment方法,将评论插入到数据库中
                    db.insertComment(comment);
                    //更新动态的评论数
                    db.updateCommentCount(dongtai.getId(), dongtai.getComment() + 1);
                    //清空评论集合中的数据
                    commentList.clear();
                    //根据动态id,从数据库中查询对应的评论数据
                    List<Comment> comments = db.getCommentsByDongtaiId(dongtai.getId());

                    //确认评论数目不同,id不同,分辨出是在不同动态之下
                    Log.d("DongtaiAdapter","id is: "+dongtai.getId()+","+"comments is : "+comments);

                    //将查询到的评论数据添加到评论集合中
                    commentList.addAll(comments);
                    //通知适配器更新视图
                    commentAdapter.notifyDataSetChanged();
                    //清空输入框的内容
                    holder.edit_text.setText("");
                }else{

                }
            }else{
                Toast.makeText(context,"评论内容不能为空!",Toast.LENGTH_SHORT).show();
            }
        });

       //在DongtaiAdapter中,为RecyclerView设置相同的布局管理器和适配器，用于显示评论的列表
        holder.commentRecyclerView.setLayoutManager(layoutManager);
        holder.commentRecyclerView.setAdapter(commentAdapter);
    }

    @Override
    public int getItemCount() {
        return dongtaiList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        EditText edit_text;//评论框
        ImageView write_image_view;//发表评论按钮
        TextView nameTextView;
        TextView contentTextView;
        ImageView avatarImageView;
        ImageView pictureImageView;
        TextView likeTextView;
        ImageView likeImageView;
        TextView comment_sayer;
        TextView comment_text_view;
        RecyclerView commentRecyclerView; //用于显示评论的RecyclerView

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name_text_view);
            contentTextView = itemView.findViewById(R.id.content_text_view);
            avatarImageView = itemView.findViewById(R.id.friend_avatar);
            pictureImageView = itemView.findViewById(R.id.picture_image_view);
            likeTextView = itemView.findViewById(R.id.like_text_view);
            likeImageView = itemView.findViewById(R.id.like_image_view);
            edit_text=itemView.findViewById(R.id.edit_text);
            write_image_view=itemView.findViewById(R.id.write_image_view);
            comment_sayer=itemView.findViewById(R.id.comment_sayer);
            comment_text_view=itemView.findViewById(R.id.comment_text_view);
            commentRecyclerView = itemView.findViewById(R.id.comment_recycler_view); //获取评论的RecyclerView控件
        }
    }

}
