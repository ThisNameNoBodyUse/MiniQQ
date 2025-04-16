package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import SQLite.UserDbHelper;
import Tools.UserInfo;

public class LoginActivity_Second extends AppCompatActivity {
    private int main_qq;

    //登录页面
    private SharedPreferences sp1;
    private SharedPreferences.Editor editor1;
    private TextView qq_forgetpwd;//忘记密码
    private EditText accountEdit;
    private EditText passwordEdit;
    private ImageView login;
    private CheckBox rememberPass;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private ImageView et_delete_account;
    private ImageView et_delete_password;
    private ImageView et_pwd_see;//密码可不可见
    private TextView register;//注册账号

    private String account_text;
    private String password_text;
    private boolean password_see;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Pretend_QQ);
        setContentView(R.layout.activity_login_second);

        int main_qq=0;//注册界面传进来的

        Intent intent1=getIntent();
        if(intent1!=null){
            main_qq=intent1.getIntExtra("user_qq",0);//注册界面传进来的
        }


        //获取控件
        qq_forgetpwd=findViewById(R.id.qq_forgetpwd);
        accountEdit = findViewById(R.id.et_account);
        passwordEdit = findViewById(R.id.et_password);
        rememberPass = findViewById(R.id.remember_password);
        register=findViewById(R.id.register) ;
        login=findViewById(R.id.login);
        et_pwd_see=findViewById(R.id.et_pwd_see);//密码可不可见
        et_delete_account=findViewById(R.id.et_delete_account);
        et_delete_password=findViewById(R.id.et_delete_password);


        //跳转忘记密码界面
        qq_forgetpwd.setOnClickListener(view -> startActivity(new Intent(LoginActivity_Second.this,Back_passwordActivity.class)));

        //账号输入状态监听
        accountEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                account_text=accountEdit.getText().toString().trim();
                password_text=passwordEdit.getText().toString().trim();
                //账号密码不为空,刷新登录按钮
                if(!TextUtils.isEmpty(account_text)&&!TextUtils.isEmpty(password_text)){
                    login.setEnabled(true);
                    login.setImageResource(R.mipmap.go_yes);
                }else{
                    login.setEnabled(false);
                    login.setImageResource(R.mipmap.go_no);
                }
            }
        });
        if(main_qq!=0){
            accountEdit.setText(String.valueOf(main_qq));
        }
        //密码输入状态监听
        passwordEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                account_text=accountEdit.getText().toString().trim();
                password_text=passwordEdit.getText().toString().trim();
                //账号密码不为空,刷新登录按钮
                if(!TextUtils.isEmpty(account_text)&&!TextUtils.isEmpty(password_text)){
                    login.setEnabled(true);
                    login.setImageResource(R.mipmap.go_yes);
                }else{
                    login.setEnabled(false);
                    login.setImageResource(R.mipmap.go_no);
                }
            }
        });
        //qq密码输入焦点监听
        passwordEdit.setOnFocusChangeListener((view, b) -> {
             if(b){
                 et_pwd_see.setVisibility(View.VISIBLE);
             }else{
                 et_pwd_see.setVisibility(View.INVISIBLE);
             }
        });
        //密码可见图标
        password_see=false;//true为可见,false为不可见
        et_pwd_see.setOnClickListener(view -> {
            if(password_see){
                //设置不可见
                passwordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                et_pwd_see.setImageResource(R.mipmap.et_pwd_no);
                passwordEdit.setSelection(passwordEdit.getText().length());
                password_see=false;
            }else{
                //设置可见
                passwordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                et_pwd_see.setImageResource(R.mipmap.et_pwd_yes);
                passwordEdit.setSelection(passwordEdit.getText().length());
                password_see=true;
            }
        });
        //点击删除图标清空内容
        et_delete_account.setOnClickListener(view -> accountEdit.setText(""));
        et_delete_password.setOnClickListener(view -> passwordEdit.setText(""));

        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        int qq=sp.getInt("main_qq",0);//记住密码的账号

        Log.d("LoginActivity_Second","qq is: "+qq);

        int user_qq;
        String username;
        String password;
        int remember_password;
        try (UserDbHelper db = UserDbHelper.getInstance(this)) {

            user_qq=db.getUserQQ(this,qq);//当前qq

            password = db.getPassword(user_qq);
            remember_password = db.getRememberPassword(user_qq);

        }
        //如果remember_password为1,说明用户选择了记住密码
        if(remember_password==1){
            judge(user_qq,password,true);
        }else{
            //调用judge方法,传入用户名,密码和false,清空EditText，并取消勾选CheckBox

            judge(user_qq,password,false);
        }
        //进入注册界面
        register.setOnClickListener(view -> startActivity(new Intent(LoginActivity_Second.this,RegisterActivity_Second.class)));

        //设置登录按钮的点击事件
        login.setOnClickListener(v -> {
            UserInfo userInfo=null;
            //获取用户输入的账号和密码
            int user_qq1= Integer.parseInt(accountEdit.getText().toString());
            String password1 = passwordEdit.getText().toString();

            //调用UserDbHelper的getInstance方法,传入当前上下文对象,获取UserDbHelper的单例对象
            UserDbHelper db1 =UserDbHelper.getInstance(LoginActivity_Second.this);
            try {
                userInfo=db1.Login(user_qq1);
            }catch (Exception e){
                Log.d("LoginActivity_Second","db1为空!");
            }


            //如果userInfo对象不为空,说明登录成功
            if(userInfo!=null){
                if(password1.equals(userInfo.getPassword())){

                    sp=getSharedPreferences("user_data",MODE_PRIVATE);
                    editor=sp.edit();
                    editor.putInt("main_qq",user_qq1);
                    editor.commit();
                    //跳转到下一个界面
                    Intent intent=new Intent(LoginActivity_Second.this, MainActivity_Second.class);
                    startActivity(intent);

                    Toast.makeText(LoginActivity_Second.this,"欢迎用户使用",Toast.LENGTH_SHORT).show();
                    finish();

                    //根据CheckBox状态决定是否更新数据库中的remember_password字段
                    if(rememberPass.isChecked()){
                        db1.updateRememberPassword(user_qq1,1);
                    }else{
                        db1.updateRememberPassword(user_qq1,0);
                    }
                }else{
                    Toast.makeText(LoginActivity_Second.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(LoginActivity_Second.this,"用户名或密码不存在!",Toast.LENGTH_SHORT).show();
            }
            db1.close();
        });
    }
    public void judge(int user_qq,String password,boolean flag){
        if(flag){
            rememberPass.setChecked(true);

            accountEdit.setText(String.valueOf(user_qq));
            passwordEdit.setText(password);
        }
    }
}