package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;
import Tools.CodeUtils;

public class Revise_passwordActivity extends AppCompatActivity {
    //找回密码界面的修改密码界面
    private EditText et_new_password;
    private EditText et_confirm_password;
    private ImageView iv_captcha;
    private EditText et_captcha;
    private Button btn_confirm;
    private Button btn_cancel;
    private int qq;
    private Boolean flag=false;
    private String new_password;
    private String confirm_password;
    private String str_captcha;
    private String captchaCode; //声明一个成员变量captchaCode
    private UserDbHelper db;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_password);

        et_new_password=findViewById(R.id.et_new_password);
        et_confirm_password=findViewById(R.id.et_confirm_password);
        iv_captcha=findViewById(R.id.iv_captcha);
        et_captcha=findViewById(R.id.et_captcha);
        btn_confirm=findViewById(R.id.btn_confirm);
        btn_cancel=findViewById(R.id.btn_cancel);

        Intent intent=getIntent();
        if(intent!=null){
            qq=intent.getIntExtra("user_qq",0);//传递进来的qq
        }

        db=UserDbHelper.getInstance(this);
        updateCaptcha();
        btn_confirm.setOnClickListener(view -> {
            new_password=et_new_password.getText().toString();
            confirm_password=et_confirm_password.getText().toString();
            str_captcha=et_captcha.getText().toString();
            if(new_password.length()<8)Toast.makeText(Revise_passwordActivity.this,"密码不能少于8位!",Toast.LENGTH_SHORT).show();
            else{
                if(!str_captcha.equalsIgnoreCase(captchaCode)){ //使用成员变量captchaCode,忽视大小写
                    Toast.makeText(Revise_passwordActivity.this,"验证码错误!",Toast.LENGTH_SHORT).show();
                    updateCaptcha();
                    Log.d("Revise_passwordActivity","验证码: "+captchaCode);
                }
                else{
                    if(new_password.equals(confirm_password)){
                        db.updatePassword(qq,new_password);
                        Toast.makeText(Revise_passwordActivity.this,"密码修改成功!",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Revise_passwordActivity.this,LoginActivity_Second.class));
                    }else{
                        Toast.makeText(Revise_passwordActivity.this,"两次密码不一致!",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        //刷新验证码
        iv_captcha.setOnClickListener(view -> {
            updateCaptcha();
        });
        //取消
        btn_cancel.setOnClickListener(view -> startActivity(new Intent(Revise_passwordActivity.this,LoginActivity_Second.class)));
    }
    private void updateCaptcha(){
        //获取CodeUtils对象
        CodeUtils codeUtils1 = new CodeUtils();
        //生成验证码图片和字符串
        Bitmap captchaImage1 = codeUtils1.createBitmap();
        String str_captcha = codeUtils1.getCode(); //获取验证码字符串
        Log.d("Revise_passwordActivity","image is: "+captchaImage1);
        Log.d("Revise_passwordActivity","验证码: "+str_captcha);
        //更新iv_captcha的内容
        iv_captcha.setImageBitmap(captchaImage1);
        captchaCode = str_captcha; //将验证码字符串赋值给成员变量captchaCode

        et_captcha.setText("");
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, LoginActivity_Second.class));
    }
}
