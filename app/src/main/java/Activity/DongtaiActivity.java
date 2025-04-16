package Activity;//DongtaiActivity.java
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.pretend_qq.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import Adapter.DongtaiAdapter;
import SQLite.UserDbHelper;
import Tools.Dongtai;
import Tools.Friend;

public class DongtaiActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private PopupWindow popupWindow;

    private int main_qq;
    private int user_id;
    private RecyclerView recyclerView;
    private DongtaiAdapter dongtaiAdapter;
    private List<Dongtai> dongtaiList;
    private ImageView backImageView;
    private ImageView addImageView;
    private UserDbHelper db;//操作数据库
    private String main_name;
    private List<Friend>friendList;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dongtai);
        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        Log.d("DongtaiActivity","main_qq is : "+main_qq);
        initView();
        initData();
        initAdapter();
        initListener();

    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler_view);//滚动
        backImageView = findViewById(R.id.back_image_view);
        addImageView = findViewById(R.id.add_image_view);
    }

    private void initData() {
        dongtaiList = new ArrayList<>();

        //将R.mipmap.genish和R.mipmap.genish_bg转化为byte数组
        byte[] genish = getByteArrayFromResource(R.mipmap.genish);
        byte[] genish_bg = getByteArrayFromResource(R.mipmap.genish_bg);
        dongtaiList.add(new Dongtai(-1,"原神", "新人上线送20抽!", genish, genish_bg, 9999, 9999, true,false));

        user_id=db.getUser_id(main_qq);

        //查询用户的动态
        Cursor cursor=db.getReadableDatabase().query("dongtai_table",null, "QQNum = ?", new String[]{String.valueOf(main_qq)}, null, null, null);
        //判断Cursor对象是否为空,如果不为空,说明查询到了数据
        if (cursor != null && cursor.getCount() > 0) {
            //使用while循环,遍历Cursor对象中的每一条记录
            while (cursor.moveToNext()) {
                main_name=db.getUsername(main_qq);
                //获取动态的ID
                @SuppressLint("Range") int dongtai_id = cursor.getInt(cursor.getColumnIndex("_id"));
                //使用getContent方法，根据动态的ID获取动态的内容
                String content = db.getContent(dongtai_id);
                byte[]avatar=db.getAvatar(main_qq);
                int like=db.getLikeCount(dongtai_id);
                int comment=db.getCommentCount(dongtai_id);
                boolean is_liked=db.getIsLike(dongtai_id);

                //使用getImage方法,根据动态的ID获取动态的图片
                byte[] image = db.getImage(dongtai_id);

                Log.d("DongtaiActivity","image is : "+image);

                boolean hasPicture;
                if(image==null){
                    hasPicture=false;
                }else{
                    hasPicture=true;
                }
                //将动态的内容和图片添加到dongtaiList集合中
                dongtaiList.add(new Dongtai(dongtai_id,main_name,content,avatar,image,like,comment,hasPicture,is_liked));
            }
        }
        friendList=db.getFriendList(user_id);
        for(Friend friend:friendList){
            int friend_qq=friend.getQQNum();
            String friend_name;
            Cursor cursor1=db.getReadableDatabase().query("dongtai_table",null, "QQNum = ?", new String[]{String.valueOf(friend_qq)}, null, null, null);
            //判断Cursor对象是否为空,如果不为空,说明查询到了数据
            if (cursor1 != null && cursor1.getCount() > 0) {
                //使用while循环,遍历Cursor对象中的每一条记录
                while (cursor1.moveToNext()) {
                    friend_name=db.getUsername(friend_qq);
                    //获取动态的ID
                    @SuppressLint("Range") int dongtai_id1 = cursor1.getInt(cursor.getColumnIndex("_id"));
                    //使用getContent方法，根据动态的ID获取动态的内容
                    String content = db.getContent(dongtai_id1);
                    byte[]avatar=db.getAvatar(friend_qq);
                    int like=db.getLikeCount(dongtai_id1);
                    int comment=db.getCommentCount(dongtai_id1);
                    boolean is_liked=db.getIsLike(dongtai_id1);

                    //使用getImage方法,根据动态的ID获取动态的图片
                    byte[] image = db.getImage(dongtai_id1);

                    Log.d("DongtaiActivity","image is : "+image);

                    boolean hasPicture;
                    if(image==null){
                        hasPicture=false;
                    }else{
                        hasPicture=true;
                    }
                    //将动态的内容和图片添加到dongtaiList集合中
                    dongtaiList.add(new Dongtai(dongtai_id1,friend_name,content,avatar,image,like,comment,hasPicture,is_liked));
                }
            }
        }
    }
    private void initAdapter(){
        Log.d("DongtaiActivity","main_qq is: "+main_qq);
        dongtaiAdapter = new DongtaiAdapter(dongtaiList, this,main_qq);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(dongtaiAdapter);
    }
    private void initListener() {

        backImageView.setOnClickListener(v -> {
            Intent intent1=new Intent(DongtaiActivity.this,MainActivity_Second.class);
            intent1.putExtra("main_qq",main_qq);
            startActivity(intent1);

        });

        addImageView.setOnClickListener(v -> {
            //显示菜单
            showPopupMenu();
        });
    }
    //显示小菜单の方法
    private void showPopupMenu() {
        //从popup_menu.xml文件中加载一个布局,用于显示小菜单
        View view = LayoutInflater.from(this).inflate(R.layout.popup_menu1, null);
        //创建一个PopupWindow对象，用于显示小菜单
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        //设置PopupWindow的背景颜色为透明
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //设置PopupWindow的显示位置为添加图标的下方
        popupWindow.showAsDropDown(addImageView);
        popupWindow.setOutsideTouchable(true); //设置PopupWindow在点击外部时消失
        //从小菜单的布局中找到说说和相册的选项，用于添加点击事件
        LinearLayout sayOption = view.findViewById(R.id.say_option);
        LinearLayout graphOption = view.findViewById(R.id.graph_option);

        sayOption.setOnClickListener(view1 -> {
            //跳转到说说的界面
            Intent intent = new Intent(DongtaiActivity.this, SayActivity.class);
            startActivity(intent); //启动SayActivity
            popupWindow.dismiss(); //关闭小菜单
        });
        graphOption.setOnClickListener(view12 -> {
            //这里可以跳转到相册的界面,实际上还没实现
            Intent intent = new Intent(DongtaiActivity.this, GraphActivity.class);
            startActivity(intent); //启动GraphActivity
            popupWindow.dismiss(); //关闭小菜单
        });
    }
    //根据资源ID获取图片的字节数组的方法
    public byte[] getByteArrayFromResource(int resId) {
        //使用Resources.getDrawable方法,根据资源ID获取Drawable对象
        Drawable drawable = getResources().getDrawable(resId);
        //使用BitmapDrawable.getBitmap方法,根据Drawable对象获取Bitmap对象
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        //使用Bitmap.compress方法,将Bitmap对象压缩为PNG格式，并输出到一个字节数组输出流中
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        //使用ByteArrayOutputStream.toByteArray方法,获取字节数组输出流中的字节数组
        byte[] byteArray = baos.toByteArray();
        //返回字节数组
        return byteArray;
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键

        Intent intent1=new Intent(DongtaiActivity.this,MainActivity_Second.class);
        intent1.putExtra("main_qq",main_qq);
        startActivity(intent1);
    }


}
