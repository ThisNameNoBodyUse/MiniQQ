package Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pretend_qq.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import Adapter.PhotoAdapter;
import SQLite.UserDbHelper;

public class SayActivity extends AppCompatActivity {

    private UserDbHelper db;

    private int main_qq;
    private SharedPreferences sp;
    private ImageView back_image_view;
    private EditText et_content;//发表文字内容的控件
    private Button btn_publish;//发表
    private ImageView select_photo;//在相册中选取的图片
    private String sentences="";//发的文字内容
    private GridView gv_photos;


    //在主活动类中，定义一个ArrayList，用来存储图片的字节数组
    //private ArrayList<byte[]> photoBytes = new ArrayList<>();
    private byte[]photoBytes=null;

    //在主活动类中，创建适配器对象，将图片数据集合和上下文传递给构造方法
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_say);

        db=UserDbHelper.getInstance(this);

        sp=getSharedPreferences("user_data",MODE_PRIVATE);
        main_qq=sp.getInt("main_qq",0);

        back_image_view=findViewById(R.id.back_image_view);
        et_content=findViewById(R.id.et_content);
        btn_publish=findViewById(R.id.btn_publish);
        select_photo=findViewById(R.id.select_photo);
        gv_photos=findViewById(R.id.gv_photos);

        //返回
        back_image_view.setOnClickListener(view -> startActivity(new Intent(SayActivity.this,DongtaiActivity.class)));
        //发表
        btn_publish.setOnClickListener(view -> {
            sentences=et_content.getText().toString();
            if(sentences.equals("")){
                Toast.makeText( SayActivity.this,"文字内容不能为空!", Toast.LENGTH_SHORT).show();
            }else{
                //这里开始,读取是否有发表图片
                if(photoBytes==null){
                    //无图片,直接发表
                     db.insertSaying(main_qq,sentences,null);
                }else{
                     Log.d("SayActivity","photoBytes is : "+photoBytes);
                     db.insertSaying(main_qq,sentences,photoBytes);
                }
                Toast.makeText(SayActivity.this,"发表成功!",Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SayActivity.this,DongtaiActivity.class));
            }
        });
        //选取照片
        select_photo.setOnClickListener(view -> {
            if(photoBytes!=null){
                Toast.makeText(SayActivity.this,"目前最多只能选取1张图片!",Toast.LENGTH_SHORT).show();
            }
            else{
                //打开系统相册或相机
                Intent intent1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //启动相册或相机,并等待返回结果
                startActivityForResult(intent1, 1);
            }

        });

    }
    //重写onActivityResult方法,用来接收选择图片的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //判断请求码和结果码是否符合要求
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //获取图片的Uri
            Uri uri = data.getData();
            try {
                //使用getContentResolver.openInputStream方法,根据Uri获取图片的输入流
                InputStream is = getContentResolver().openInputStream(uri);
                //使用BitmapFactory.decodeStream方法,根据输入流将图片转换为Bitmap对象
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                //使用Bitmap.compress方法,将Bitmap对象压缩为JPEG格式,并输出到一个字节数组输出流中
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //使用ByteArrayOutputStream.toByteArray方法,获取字节数组输出流中的字节数组
                photoBytes= baos.toByteArray();
                //在主活动类中,创建适配器对象,将图片字节数组集合和上下文传递给构造方法
                photoAdapter = new PhotoAdapter(photoBytes, this);
                //在主活动类中,使用setAdapter方法,将适配器设置到GridView控件上
                gv_photos.setAdapter(photoAdapter);
                Log.d("SayActivity","photoBytes is :"+photoBytes);
                //关闭输入流
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //通知适配器更新数据
            photoAdapter.notifyDataSetChanged();
        }
    }
    @Override
    public void onBackPressed(){
        //自定义返回按键
        startActivity(new Intent(this, DongtaiActivity.class));
    }
}