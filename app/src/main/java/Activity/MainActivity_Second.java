package Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.pretend_qq.R;
import com.journeyapps.barcodescanner.CaptureActivity;

import Fragment.ContactFragment;
import Fragment.DongtaiFragment;
import Fragment.MessageFragment;
import SQLite.UserDbHelper;


public class MainActivity_Second extends AppCompatActivity {
    private UserDbHelper db;
    private SharedPreferences sp;
    private int main_qq;//主体qq
    private TextView title;//标题
    private ImageView add;//添加
    private ImageView avatar;//头像
    private TextView message;//消息,底部
    private TextView friend;//联系人,底部
    private TextView dongtai;//动态,底部

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_second);
        db=UserDbHelper.getInstance(this);

        /*
        获取登录界面传进来的QQ号码为主体
         */
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);

        SharedPreferences.Editor editor = getSharedPreferences("user_data",MODE_PRIVATE).edit();
        editor.putInt("main_qq",main_qq);
        editor.commit();
        title = findViewById(R.id.title);
        add = findViewById(R.id.add);
        avatar = findViewById(R.id.avatar);
        message = findViewById(R.id.message);
        friend = findViewById(R.id.friend);
        dongtai = findViewById(R.id.dongtai);
        //头像
        byte[]avatar1=db.getAvatar(main_qq);
        Bitmap bitmap= BitmapFactory.decodeByteArray(avatar1,0,avatar1.length);
        avatar.setImageBitmap(bitmap);
        //头像点击,跳转到第一个设置界面
        avatar.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity_Second.this,SettingActivity_Second.class));
        });

        //初始默认消息处点亮
        title.setText("消息");
        message.setSelected(true);
        MessageFragment messageFragment1 = new MessageFragment(); //创建一个MessageFragment对象
        FragmentManager fragmentManager1 = getSupportFragmentManager(); //获取FragmentManager对象
        FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction(); //开启一个事务
        fragmentTransaction1.replace(R.id.fragment_content, messageFragment1); //用MessageFragment替换child_fragment_container
        fragmentTransaction1.commit(); //提交事务


        message.setOnClickListener(v -> {
            MessageFragment messageFragment = new MessageFragment(); //创建一个MessageFragment对象
            FragmentManager fragmentManager = getSupportFragmentManager(); //获取FragmentManager对象
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //开启一个事务
            fragmentTransaction.replace(R.id.fragment_content, messageFragment); //用MessageFragment替换child_fragment_container
            fragmentTransaction.commit(); //提交事务
            title.setText("消息");
            message.setSelected(true);
            friend.setSelected(false);
            dongtai.setSelected(false);

        });


        friend.setOnClickListener(v -> {
            message.setSelected(false);
            friend.setSelected(true);
            dongtai.setSelected(false);
            title.setText("联系人");
            ContactFragment contactFragment = new ContactFragment(); //创建一个FriendListFragment对象
            FragmentManager fragmentManager = getSupportFragmentManager(); //获取FragmentManager对象
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction(); //开启一个事务
            fragmentTransaction.replace(R.id.fragment_content, contactFragment); //用FriendListFragment替换child_fragment_container
            fragmentTransaction.commit(); //提交事务
        });


        dongtai.setOnClickListener(v -> {
            message.setSelected(false);
            friend.setSelected(false);
            dongtai.setSelected(true);
            title.setText("动态");
            DongtaiFragment dongtaiFragment = new DongtaiFragment();//创建一个DongtaiFragment对象
            FragmentManager fragmentManager = getSupportFragmentManager();//获取FragmentManager对象
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();//开启一个事务
            fragmentTransaction.replace(R.id.fragment_content, dongtaiFragment);//用DongtaiFragment替换fragment_content
            fragmentTransaction.commit(); //提交事务
        });

        add.setOnClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(MainActivity_Second.this, add);//创建一个PopupMenu对象
            popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());//加载popup_menu.xml布局文件
            //设置菜单项的点击事件监听器
            popupMenu.setOnMenuItemClickListener(item -> {
                        int select_id = item.getItemId();

                        if (select_id == R.id.create_group) {

                        } else if (select_id == R.id.add_friend) {
                            Intent intent=new Intent(MainActivity_Second.this,SearchActivity.class);
                            intent.putExtra("main_qq",main_qq);
                            startActivity(intent);
                        } else if (select_id == R.id.scan) {
                            //实现扫一扫
                            Intent intent = new Intent(MainActivity_Second.this, CaptureActivity.class); //创建一个Intent对象
                            int requestCode = 100; //设置一个请求码
                            startActivityForResult(intent, requestCode);//启动CaptureActivity类

                        } else {
                            popupMenu.dismiss();
                        }

                        return true;
                    }
            );
            popupMenu.show(); //显示弹出菜单
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            //判断请求码和结果码是否匹配
            if (data != null) { //判断Intent对象是否为空
                String content = data.getStringExtra("SCAN_RESULT"); //获取扫描结果

            }
        }
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, LoginActivity_Second.class));
    }
}
