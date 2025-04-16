package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class Main_to_RevisePassword_Activity extends AppCompatActivity {
    //直接在登录后修改密码的界面
    private String str_old_password;//旧密码
    private String str_new_password="";//新密码
    private String str_confirm_new_password="";//确认密码
    private int main_qq;
    private UserDbHelper db;
    private SharedPreferences sp;
    private EditText new_password;//新密码文本框
    private EditText confirm_new_password;//确认密码文本框
    private Button confirm;//确认按钮

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_to_revise_password);
        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        str_old_password=db.getPassword(main_qq);//旧密码

        //初始化控件
        new_password=findViewById(R.id.new_password);
        confirm_new_password=findViewById(R.id.confirm_new_password);
        confirm=findViewById(R.id.confirm);

        //监听按钮
        confirm.setOnClickListener(view -> {
            str_new_password=new_password.getText().toString();
            str_confirm_new_password=confirm_new_password.getText().toString();

            if(str_new_password.equals("")){
                Toast.makeText(Main_to_RevisePassword_Activity.this,"请输入新密码!",Toast.LENGTH_SHORT).show();
            }else{
                if(str_confirm_new_password.equals("")){
                    Toast.makeText(Main_to_RevisePassword_Activity.this,"请确认新密码!",Toast.LENGTH_SHORT).show();
                }else{
                    if(!is_count_range(str_new_password)){
                        Toast.makeText(Main_to_RevisePassword_Activity.this,"新密码长度要8-16位!",Toast.LENGTH_SHORT).show();
                    }else{
                        if(!str_new_password.matches(".*\\d.*")){
                            Toast.makeText(Main_to_RevisePassword_Activity.this,"新密码至少包含1个数字!",Toast.LENGTH_SHORT).show();
                        }else{
                            if(!str_new_password.matches(".*[a-zA-Z].*")){
                                Toast.makeText(Main_to_RevisePassword_Activity.this,"新密码至少包含一个字母!",Toast.LENGTH_SHORT).show();
                            }else{
                                if(!str_new_password.equals(str_confirm_new_password)){
                                    Toast.makeText(Main_to_RevisePassword_Activity.this,"两次密码不一致!",Toast.LENGTH_SHORT).show();
                                }else if(str_new_password.equals(str_old_password)){
                                    Toast.makeText(Main_to_RevisePassword_Activity.this,"新密码不能与旧密码相同!",Toast.LENGTH_SHORT).show();
                                }else{
                                    db.updatePassword(main_qq,str_new_password);//修改密码
                                    Toast.makeText(Main_to_RevisePassword_Activity.this,"修改成功!",Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(Main_to_RevisePassword_Activity.this,Account_Safe_Activity.class));
                                }
                            }
                        }
                    }
                }
            }
        });

    }
    public void go_to_account_safe(View view){
        //回去账号安全中心界面
        startActivity(new Intent(Main_to_RevisePassword_Activity.this,Account_Safe_Activity.class));
    }

    public boolean is_count_range(String password){
        int len=password.length();
        if(len<8||len>16)return false;
        return true;
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, Account_Safe_Activity.class));
    }
}