package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class SearchActivity extends AppCompatActivity {
    private ImageView btn_back;
    private UserDbHelper db;//操作数据库查询是否存在
    private int main_qq;//主体qq
    private EditText et_user_qq;
    private Button btn_search_friend;
    private String str_find_qq="";
    private int find_qq;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        //传递主体qq
        Intent intent=getIntent();
        if(intent!=null){
            main_qq=intent.getIntExtra("main_qq",0);
        }

        db=UserDbHelper.getInstance(this);
        et_user_qq=findViewById(R.id.et_user_qq);
        btn_search_friend=findViewById(R.id.btn_search_friend);
        btn_back=findViewById(R.id.btn_back);

        btn_search_friend.setOnClickListener(view -> {
            str_find_qq=et_user_qq.getText().toString();
            if(str_find_qq.equals("")){
                Toast.makeText(SearchActivity.this,"请输入QQ号码!",Toast.LENGTH_SHORT).show();
            }else{
                find_qq=Integer.parseInt(str_find_qq);
                if(db.getUserQQ(this,find_qq)!=0){
                    Intent intent1=new Intent(SearchActivity.this,UserInfoActivity.class);
                    intent1.putExtra("main_qq",main_qq);
                    intent1.putExtra("find_qq",find_qq);
                    startActivity(intent1);
                }else{
                    Toast.makeText(SearchActivity.this,"查无此用户!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn_back.setOnClickListener(view -> {
            Intent intent1=new Intent(SearchActivity.this,MainActivity_Second.class);
            intent1.putExtra("main_qq",main_qq);
            startActivity(intent1);
        });
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        Intent intent1=new Intent(SearchActivity.this,MainActivity_Second.class);
        intent1.putExtra("main_qq",main_qq);
        startActivity(intent1);
    }
}