package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.pretend_qq.R;

public class Account_Safe_Activity extends AppCompatActivity {
    //用户安全中心

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_safe);
    }
    public void go_to_revise(View view){
        startActivity(new Intent(Account_Safe_Activity.this,Main_to_RevisePassword_Activity.class));
    }
    public void to_Setting_Third(View view){
        startActivity(new Intent(Account_Safe_Activity.this,SettingActivity_Third.class));
    }
    public void go_to_cancel_account(View view){
        startActivity(new Intent(Account_Safe_Activity.this,Cancel_Account_Activity.class));
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, SettingActivity_Third.class));
    }
}