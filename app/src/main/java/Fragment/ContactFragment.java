package Fragment;



import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;


import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pretend_qq.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import Adapter.FriendAdapter;
import SQLite.UserDbHelper;
import Tools.Friend;

public class ContactFragment extends Fragment {
    //主界面中的联系人Fragment
    private int user_id;
    private int main_qq;
    private SharedPreferences sp;
    private UserDbHelper db;
    private RecyclerView friendRecyclerView; //联系人列表
    private SearchView searchView; //搜索框
    private List<Friend> friendList; //联系人数据
    private FriendAdapter friendAdapter; //联系人适配器

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sp= getActivity().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        main_qq=sp.getInt("main_qq",0);
        db = UserDbHelper.getInstance(getContext());
        user_id=db.getUser_id(main_qq);//主qq所在user_table中的id


        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        //初始化控件
        friendRecyclerView = view.findViewById(R.id.friend_recycler_view);
        searchView = view.findViewById(R.id.search_view);
        //初始化数据
        friendList = new ArrayList<>();
        //测试数据

//        friendList.add(new Friend("张三",  "离线",  getByteArrayFromResource(R.drawable.avatar1)));
//        friendList.add(new Friend("李四",  "离线",  getByteArrayFromResource(R.drawable.avatar2)));
//        friendList.add(new Friend("王五",  "忙碌", getByteArrayFromResource(R.drawable.avatar3)));

        //初始化适配器
        friendAdapter = new FriendAdapter(user_id, getActivity());
        //为RecyclerView设置适配器和布局管理器
        friendRecyclerView.setAdapter(friendAdapter);
        friendRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        //搜索框添加监听器实现搜索功能
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //当用户提交搜索时,执行搜索操作
                searchFriend(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //当用户输入搜索内容时,执行搜索操作
                  searchFriend(newText);
                  return true;
            }
        });
        return view;
    }



    //搜索联系人的方法
    private void searchFriend(String name) {
        //清空原来的数据
        friendList.clear();
        List<Friend> friends = db.getAllFriends(user_id);
        //如果搜索名字为空,就显示所有的联系人
        if(name.isEmpty()){
            //将查询到的联系人添加到列表中
            friendList.addAll(friends);
            Log.d("ContactFragment","名字为空");
            friendAdapter.update(friends);
        }else{
            Log.d("ContactFragment","名字不为空");
            //根据关键字搜索
            for (Friend friend : friends) {
                //获取联系人的名字
                String friendName = friend.getName();
                Log.d("ContactFragment","friendName is *: "+friendName);
                //判断联系人的名字是否包含关键字
                if (friendName.contains(name)) {
                    //如果包含,就添加到新的列表中
                    Log.d("ContactFragment","name is : "+name);
                    friendList.add(friend);
                }
            }
            //新的列表为空,空白,不理
            if (friendList.isEmpty()) {

            }
            //通知适配器更新视图
            friendAdapter.update(friendList);
        }
    }

    //定义一个方法,用来根据资源ID获取图片的字节数组
    public byte[] getByteArrayFromResource(int resId) {
        //使用Resources.getDrawable方法,根据资源ID获取Drawable对象
        Drawable drawable = getResources().getDrawable(resId);
        //使用BitmapDrawable.getBitmap方法,根据Drawable对象获取Bitmap对象
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        //使用Bitmap.compress方法,将Bitmap对象压缩为PNG格式,并输出到一个字节数组输出流中
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        //使用ByteArrayOutputStream.toByteArray方法,获取字节数组输出流中的字节数组
        byte[] byteArray = baos.toByteArray();
        //返回字节数组
        return byteArray;
    }

}



