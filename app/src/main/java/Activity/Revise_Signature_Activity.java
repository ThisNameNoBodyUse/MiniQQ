package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class Revise_Signature_Activity extends AppCompatActivity {
    //修改签名的界面

    private String str_old_signature;//原先签名
    private String str_signature="";//控件中的签名
    private UserDbHelper db;
    private SharedPreferences sp;
    private int main_qq;
    private ImageView img_back;
    private ImageView img_save;
    private EditText et_signature;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_signature);
        et_signature=findViewById(R.id.et_signature);
        db=UserDbHelper.getInstance(this);
        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);
        str_old_signature=db.getSignature(main_qq);
        et_signature.setText(str_old_signature);

        img_save=findViewById(R.id.img_save);
        img_back=findViewById(R.id.img_back);
        //返回上一个界面
        img_back.setOnClickListener(view -> startActivity(new Intent(Revise_Signature_Activity.this, Revise_Message_Activity.class)));
        //保存签名
        img_save.setOnClickListener(view -> {
            str_signature=et_signature.getText().toString();
            if(str_signature.equals("")){
                Toast.makeText(Revise_Signature_Activity.this, "签名不能为空!", Toast.LENGTH_SHORT).show();
            }else{
                db.updateSignature(main_qq,str_signature);
                Toast.makeText(Revise_Signature_Activity.this, "修改成功!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Revise_Signature_Activity.this,Revise_Message_Activity.class));
            }
        });
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, Revise_Message_Activity.class));
    }
}