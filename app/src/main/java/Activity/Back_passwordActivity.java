package Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretend_qq.R;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import SQLite.UserDbHelper;

public class Back_passwordActivity extends AppCompatActivity {
    private Button answer_confirm;//确认密保
    private EditText qq_number;
    private Button qq_confirm;
    private Button cancel;
    private TextView question1;
    private EditText answer1;
    private TextView question2;
    private EditText answer2;
    private UserDbHelper db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_password);
        qq_number=findViewById(R.id.qq_number);
        qq_confirm=findViewById(R.id.qq_confirm);
        answer_confirm=findViewById(R.id.answer_confirm);

        cancel=findViewById(R.id.cancel);
        question1=findViewById(R.id.question1);
        answer1=findViewById(R.id.answer1);
        question2=findViewById(R.id.question2);
        answer2=findViewById(R.id.answer2);

        db=UserDbHelper.getInstance(this);
        //取消
        cancel.setOnClickListener(view -> startActivity(new Intent(Back_passwordActivity.this,LoginActivity_Second.class)));
        //确认
        qq_confirm.setOnClickListener(view -> {
            String s = qq_number.getText().toString();
            if (!s.equals("")) {
                int qq = Integer.parseInt(s);
                int query_qq = db.getUserQQ(this, qq);
                if (query_qq!=0){
                    //question1.setVisibility(View.VISIBLE);
                    answer1.setVisibility(View.VISIBLE);
                    // question2.setVisibility(View.VISIBLE);
                    answer2.setVisibility(View.VISIBLE);
                    answer_confirm.setVisibility(View.VISIBLE);
                    qq_number.setEnabled(false);
                    //获取密保1
                    String str_question1 = db.getQuestion1(query_qq);
                    Log.d("Back_passwordActivity", "str_question1 is: " + str_question1);
                    question1.setText(str_question1);
                    question1.setVisibility(View.VISIBLE);
                    //密保1答案
                    String str_answer1 = db.getAnswer1(query_qq);
                    Log.d("Back_passwordActivity", "str_answer1 is: " + str_answer1);
                    //获取密保2
                    String str_question2 = db.getQuestion2(query_qq);
                    Log.d("Back_passwordActivity", "str_question2 is: " + str_question2);
                    question2.setText(str_question2);
                    question2.setVisibility(View.VISIBLE);
                    //密保2答案
                    String str_answer2 = db.getAnswer2(query_qq);
                    Log.d("Back_passwordActivity", "str_answer2 is: " + str_answer2);
                    answer_confirm.setOnClickListener(view1 -> {
                        String str_in_answer1;
                        str_in_answer1 = answer1.getText().toString();//输入的答案1

                        String str_in_answer2;
                        str_in_answer2 = answer2.getText().toString();//输入的答案2

                        if(str_in_answer1.equals("") || str_in_answer2 .equals("")){
                            Toast.makeText(Back_passwordActivity.this, "请回答您的密保问题!", Toast.LENGTH_SHORT).show();
                        }else{

                            if(!str_in_answer1.equals(str_answer1)||!str_in_answer2.equals(str_answer2)){
                                Toast.makeText(Back_passwordActivity.this, "您输入的答案有误!", Toast.LENGTH_SHORT).show();
                            }else{
                                //跳入修改密码界面
                                 Intent intent=new Intent(Back_passwordActivity.this,Revise_passwordActivity.class);
                                 intent.putExtra("user_qq",query_qq);
                                 startActivity(intent);
                                    }
                                }
                            });
                        }else{
                    Toast.makeText(Back_passwordActivity.this,"查询不到此用户!",Toast.LENGTH_SHORT).show();
                    qq_number.setText("");
                }
                    }
                });
            }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, LoginActivity_Second.class));
    }
}