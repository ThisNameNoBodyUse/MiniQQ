package Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import SQLite.UserDbHelper;

public class Revise_username_Activity extends AppCompatActivity {
    //修改昵称
    private String old_username;
    private UserDbHelper db;
    private SharedPreferences sp;
    private EditText et_username;
    private TextView no;
    private TextView ok;
    private int main_qq;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revise_username);
        //初始化控件
        et_username = findViewById(R.id.et_username);
        no = findViewById(R.id.no);
        ok = findViewById(R.id.ok);

        db = UserDbHelper.getInstance(this);
        sp = getSharedPreferences("user_data", MODE_PRIVATE);
        main_qq = sp.getInt("main_qq", 0);
        //旧昵称
        old_username = db.getUsername(main_qq);
        et_username.setText(old_username);
        //获取图标的引用
        Drawable cancel = getResources().getDrawable(R.mipmap.cancel);

        //给文本框设置一个触摸监听器
        et_username.setOnTouchListener((v, event) -> {
            //如果触摸的动作是抬起手指
            if (event.getAction() == MotionEvent.ACTION_UP) {
                //获取触摸的位置
                float x = event.getX();
                float y = event.getY();
                //获取文本框的宽度和高度
                int width = et_username.getWidth();
                int height = et_username.getHeight();
                //获取图标的宽度和高度
                int iconWidth = cancel.getIntrinsicWidth();
                int iconHeight = cancel.getIntrinsicHeight();
                //计算图标的边界
                int left = width - iconWidth - et_username.getPaddingRight();
                int right = width - et_username.getPaddingRight();
                int top = (height - iconHeight) / 2;
                int bottom = top + iconHeight;
                //判断触摸的位置是否在图标的边界内
                if (x >= left && x <= right && y >= top && y <= bottom) {
                    // 如果是的话,清空文本框的内容
                    et_username.setText("");
                    //返回true表示消费了这个事件
                    return true;
                }
            }
            // 返回false表示不消费这个事件
            return false;
        });

        //给文本框添加一个文本改变监听器
        et_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // 在文本改变之前,不做任何操作
            }
            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                //在文本改变的时候,根据文本框的内容长度,动态地设置或清除右边的图标
                if (s.length() > 0) {
                    //如果文本框有内容,设置右边的图标
                    et_username.setCompoundDrawablesWithIntrinsicBounds(null, null, cancel, null);
                } else {
                    //如果文本框无内容,清除右边的图标
                    et_username.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //文本改变后不做任何操作
            }


        });

        no.setOnClickListener(view -> startActivity(new Intent(Revise_username_Activity.this,Revise_Message_Activity.class)));
        ok.setOnClickListener(view -> {
            String str_username=et_username.getText().toString();
            if(str_username.equals("")){
                Toast.makeText(this, "昵称不能为空!", Toast.LENGTH_SHORT).show();
            }else{
                db.updateUsername(main_qq,str_username);
                db.updateUsername_in_friend_table(main_qq,str_username);//friend_table中的信息
                startActivity(new Intent(Revise_username_Activity.this,Revise_Message_Activity.class));
            }
        });
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, Revise_Message_Activity.class));
    }
}