package Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.pretend_qq.R;

import java.util.ArrayList;

//定义一个自定义的适配器类,继承自BaseAdapter
//qq空间发表图片的适配器
public class PhotoAdapter extends BaseAdapter {
    //定义一个ArrayList，用来存储图片的字节数组
    private byte []photoBytes;
    //定义一个上下文对象
    private Context context;

    //定义一个构造方法,传递图片数据集合和上下文
    public PhotoAdapter(byte[] photoBytes, Context context) {
        Log.d("SayActivity","photoBytes* is :"+photoBytes);
        this.photoBytes = photoBytes;
        this.context = context;
    }

    //重写getCount方法,返回字节数组的长度
    @Override
    public int getCount() {
        //判断photoBytes数组是否为空,如果为空,就返回0,否则返回它的长度
        return photoBytes == null ? 0 : 1;
    }


    //重写getItem方法,返回图片数据集合中的某个元素
    @Override
    public Object getItem(int position) {
        return photoBytes[position];
    }

    //重写getItemId方法,返回某个位置的ID
    @Override
    public long getItemId(int position) {
        return position;
    }

    //重写getView方法,将图片数据绑定到GridView的每个子项上
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //使用LayoutInflater将子布局文件（item.xml）转换为View对象
        View view = LayoutInflater.from(context).inflate(R.layout.photo_item, null);

        ImageView imageView = view.findViewById(R.id.iv_photo);

        byte[] byteArray = photoBytes;

        Log.d("SayActivity","photoBytes111 is :"+photoBytes);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.d("SayActivity","bitmap** is :"+bitmap);

        imageView.setImageBitmap(bitmap);
        //返回View对象
        return view;
    }
}