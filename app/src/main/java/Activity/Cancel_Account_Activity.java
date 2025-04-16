package Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class Cancel_Account_Activity extends AppCompatActivity {
    //注销账号的界面
    private UserDbHelper db;
    private SharedPreferences sp;
    private int main_qq;
    private Button cancel_account;//注销按钮
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_account);
        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        //初始化按钮
        cancel_account=findViewById(R.id.cancel_account);
        cancel_account.setOnClickListener(view -> {
            AlertDialog.Builder builder=new AlertDialog.Builder(Cancel_Account_Activity.this);
            builder.setTitle("注销账号");
            builder.setMessage("您确定要注销账号吗?");
            builder.setPositiveButton("是", (dialogInterface, i) -> {
                //获取一个新的数据库实例
                UserDbHelper db = UserDbHelper.getInstance(Cancel_Account_Activity.this);
                db.deleteUserByQQNum(main_qq, db.getUser_id(main_qq));
                Toast.makeText(Cancel_Account_Activity.this,"注销成功!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Cancel_Account_Activity.this, LoginActivity_Second.class));
            });
            builder.setNegativeButton("否", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
            builder.create().show();
        });

    }
    public void go_to_account_safe(View view){
        //回去账号安全中心界面
        startActivity(new Intent(Cancel_Account_Activity.this,Account_Safe_Activity.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, Account_Safe_Activity.class));
    }
}