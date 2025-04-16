package Fragment;// 导入所需的包
import android.content.Intent;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.fragment.app.Fragment;

import com.example.pretend_qq.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Activity.DongtaiActivity;
import Activity.MainActivity_Second;

public class DongtaiFragment extends Fragment {
    //主界面中的动态Fragment

    private ListView listView;
    private String[] texts; //存放ListView中每一行的文本内容
    private int[] icons = { //存放ListView中每一行的图标资源id
            R.drawable.icon_friend,
            R.drawable.icon_game,
            R.drawable.icon_group,
            R.drawable.icon_nearby,
            R.drawable.icon_market,
            R.drawable.icon_card,
            R.drawable.icon_live,
            R.drawable.icon_anime,
            R.drawable.icon_read,
            R.drawable.icon_shop,
            R.drawable.icon_music,
            R.drawable.icon_sport
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dongtai, container, false);

        listView = rootView.findViewById(R.id.list_view);
        texts = getResources().getStringArray(R.array.dongtai_items); //从strings.xml中获取文本内容

        List<Map<String, Object>> listItems = new ArrayList<>(); //创建一个List集合,用于存放Map对象
        for (int i = 0; i < texts.length; i++) {
            Map<String, Object> listItem = new HashMap<>(); //创建一个Map对象,用于存放图标和文本
            listItem.put("icon", icons[i]); //将图标资源id放入Map对象中
            listItem.put("text", texts[i]); //将文本内容放入Map对象中
            listItems.add(listItem); //将Map对象添加到List集合中
        }

        SimpleAdapter adapter = new SimpleAdapter(getActivity(), //创建一个SimpleAdapter对象，用于将List集合中的数据绑定到ListView中
                listItems, //要绑定的数据源
                R.layout.item_main_dongtai, //每一行的布局
                new String[]{"icon", "text"}, //Map对象中的两个键名
                new int[]{R.id.icon, R.id.dongtai_items} //要绑定的两个控件的id
        );

        listView.setAdapter(adapter); //将适配器设置给ListView

        //设置ListView的点击事件,当点击某一行时,根据文本内容判断是否跳转到DongtaiActivity界面
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的行的文本内容
                String text = texts[position];
                //如果文本内容是好友动态,就跳转到DongtaiActivity界面
                if (text.equals("好友动态")) {
                    //创建一个Intent对象,用于指定要跳转的目标界面
                    Intent intent = new Intent(getActivity(), DongtaiActivity.class);
                    //启动目标界面
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

}
