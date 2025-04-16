package SQLite;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import Tools.Comment;
import Tools.Dongtai;
import Tools.Friend;
import Tools.UserInfo;


//定义一个UserDbHelper类，继承自SQLiteOpenHelper类，用于操作SQLite数据库
public class UserDbHelper extends SQLiteOpenHelper {
    // 声明一个 Context 类型的成员变量
    private Context mContext;
    //声明一个UserDbHelper的单例对象,用于保证只有一个实例存在
    private static UserDbHelper sHelper;
    //定义数据库的名称和版本
    private static final String DB_NAME="user_info.db";
    //将VERSION改为2,这样就会触发SQLiteOpenHelper类中的onUpgrade方法的调用,执行ALTER TABLE语句
    private static final int VERSION =1;

    //构造方法,调用父类的构造方法,传入数据库的名称和版本
    public UserDbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    //创建单例,供使用调用该类里的增删改查的方法
    public synchronized static UserDbHelper getInstance(Context context){
        //单例对象为空,就new一个新的对象
        if(sHelper==null){
            sHelper=new UserDbHelper(context,DB_NAME,null,VERSION);
        }
        //返回单例对象
        return sHelper;
    }

    @Override
    //onCreate方法是UserDbHelper类的一个回调方法
    // 在数据库第一次创建时才会调用
    public void onCreate(SQLiteDatabase db) {
        //user_table表
        db.execSQL("create table if not exists user_table(_id integer primary key autoincrement," +
                "QQNum Integer,"+//qq号码
                "username text," +//用户名,类型为文本
                "password text," +//密码,类型为文本
                "remember_password integer," +//是否记住密码,类型为整型
                "avatar BLOB," +//头像,类型为BLOB
                "gender text default '未知'," +//性别,类型为文本,默认值为'未知'
                "year Integer default '未知',"+//出生年份
                "month Integer default '未知',"+//出生月
                "day Integer default '未知',"+//出生日
                "region text default '未知'," + //地区,类型为文本,默认值为'未知'
                "question1 text,"+//密保问题1
                "question_answer1 text,"+//密保1答案
                "question2 text,"+//密保问题2
                "question_answer2 text,"+//密保2答案
                "signature text default ' '" +  //签名,类型为文本,默认值为空
                ")");

        //friend表,存储彼此之间的关系
        db.execSQL("create table if not exists friend(_id integer primary key autoincrement," +
                "user_id integer," +  //用户的ID,类型为整型
                "friend_id integer" + //好友的ID,类型为整型
                ")");

        //friend_table,存储好友qq,用户名,头像,在线状态,用于联系人页面读取
        db.execSQL("create table if not exists friend_table(_id integer primary key autoincrement," +
                "user_id integer," + //用户的id
                "friend_id integer," + //好友的id
                "QQNum integer,"+ //好友qq
                "friend_name text,"+ //好友用户名
                "avatar BOLB,"+ //好友头像
                "status text default '离线'"+ //好友在线状态
                ", unique(user_id, friend_id))"); //联合唯一约束


        db.execSQL("create table if not exists message_table(_id integer primary key autoincrement," +
                "sender_id integer," +  //发送者的ID,外键,引用 user_table的_id
                "receiver_id integer," + //接收者的ID,外键,引用user_table的_id
                "content text," + //消息的内容
                "type integer," +//消息的类型,表示是发送还是接收
                "time text"+ //消息时间
                ")");

        db.execSQL("create table if not exists dongtai_table(_id integer primary key autoincrement," +
                "QQNum integer," +  //动态的发布者的QQ号,外键,引用user_table的QQNum
                "content text," + //动态的内容
                "image BLOB," + //动态的图片
                "like_count integer," + //动态的点赞数
                "comment_count integer," + //动态的评论数
                "is_like boolean" + //动态是否被当前用户点赞
                ")");

        db.execSQL("create table if not exists comment_table(_id integer primary key autoincrement," +
                "dongtai_id integer," +  //评论的动态的ID,外键,引用dongtai_table的_id
                "user_id integer," +  //评论的用户的ID,引用user_table的_id
                "content text"+ ")"//评论的内容
                );
        
    }
    //定义一个添加好友的方法
    public void add_friend(int user_id,int friend_id,Friend friend,Friend me){
        //获取一个可写数据库对象
        SQLiteDatabase db=UserDbHelper.getInstance(mContext).getWritableDatabase();
        //向friend表中插入一条好友关系,如果不存在的话
        db.execSQL("INSERT INTO friend (user_id, friend_id) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM friend WHERE user_id = ? AND friend_id = ?)", new Object[]{user_id, friend_id, user_id, friend_id});
        db.execSQL("INSERT INTO friend (user_id, friend_id) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM friend WHERE user_id = ? AND friend_id = ?)", new Object[]{friend_id, user_id, friend_id, user_id});
        //获取好友的QQ号码,姓名,头像,在线状态
        int QQNum = friend.getQQNum();
        String friend_name = friend.getName();
        byte[] avatar = friend.getAvatar();
        String status = friend.getStatus();
        // 使用ContentValues对象来存储好友信息
        ContentValues values = new ContentValues();
        values.put("user_id",user_id);
        values.put("friend_id",friend_id);
        values.put("QQNum", QQNum); //存储好友的QQ号码

        Log.d("UserDbHelper","Friend's QQNum is : "+QQNum);

        values.put("friend_name", friend_name); //存储好友的姓名
        values.put("avatar", avatar); //存储好友的头像
        values.put("status", status); //存储好友的在线状态
        //向friend_table表中插入一条好友信息
        db.insertWithOnConflict("friend_table", null, values, SQLiteDatabase.CONFLICT_IGNORE);

        //获取本人的QQ号码,姓名,头像,在线状态
        int QQNum1 = me.getQQNum();
        String friend_name1 = me.getName();
        byte[] avatar1 = me.getAvatar();
        String status1 = me.getStatus();
        //使用ContentValues对象来存储好友信息
        ContentValues values1 = new ContentValues();
        values1.put("user_id",friend_id);
        values1.put("friend_id",user_id);
        values1.put("QQNum", QQNum1); //存储本人的QQ号码

        Log.d("UserDbHelper","My QQNum is : "+QQNum1);
        values1.put("friend_name", friend_name1); //存储本人的姓名

        values1.put("avatar", avatar1); //存储本人的头像
        values1.put("status", status1); //存储本人的在线状态
        //向friend_table表中插入一条好友信息,如果不存在的话
        db.insertWithOnConflict("friend_table", null, values, SQLiteDatabase.CONFLICT_IGNORE);
        db.insertWithOnConflict("friend_table", null, values1, SQLiteDatabase.CONFLICT_IGNORE);

        //关闭数据库对象
        db.close();
    }

    //定义一个函数来根据QQ号码注销用户
    public void deleteUserByQQNum(int QQNum,int user_id) {
        //获取数据库实例
        SQLiteDatabase db = this.getWritableDatabase();
        int friend_id=user_id;
        //定义一个字符串变量来存储删除语句
        String deleteQuery = "DELETE FROM user_table WHERE QQNum = ?";
        String deleteQuery1= "DELETE FROM friend_table WHERE friend_id = ?";
        //执行删除语句，传入QQ号码作为参数
        db.execSQL(deleteQuery, new Object[]{QQNum});
        db.execSQL(deleteQuery1,new Object[]{friend_id});
        //关闭数据库连接
        db.close();
    }


    // 定义一个根据好友的QQ号码来获取好友的id的方法
    public int getFriendIdByQQNum(int QQNum) {
        // 定义一个变量,用于存储好友的id,从friend_table中获取
        int friend_id = -1;
        // 获取一个可读数据库对象
        SQLiteDatabase db = UserDbHelper.getInstance(mContext).getReadableDatabase();
        // 使用Cursor对象接收查询结果
        Cursor cursor = null;
        try {
            // 使用query方法来查询friend_table表中的id，根据QQNum列来筛选
            cursor = db.query("friend_table", new String[]{"_id"}, "QQNum=?", new String[]{String.valueOf(QQNum)}, null, null, null);
            // 如果查询到了好友的id,就将其赋值给变量
            if (cursor.moveToFirst()) {
                friend_id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 最后，关闭cursor和db对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        // 返回好友的id
        return friend_id;
    }



    public void add_init_friend(SQLiteDatabase db){
        //此方法我建立用于为所有用户添加四个初始好友
        //定义一个数组，用来存储四个初始好友的用户名

        //String[]friends={"QQ管家","QQ会员","QQ团队","QQ手游"};
        String[]friends={"00001","00002","00003","00004"};
        //使用 query 方法来查询 user_table 表中的所有用户的 id
        Cursor cursor=db.query("user_table",new String[]{"_id"}, null, null, null, null, null);
        //判断 Cursor 对象是否为空,如果不为空,说明查询到了数据
        if(cursor!=null&&cursor.getCount()>0){
            //开始一个事务
            db.beginTransaction();
            try{
                //移动Cursor对象到第一条记录
                cursor.moveToFirst();
                //遍历Cursor对象,取出每个用户的 id
                do{
                    //获取用户的 id
                    @SuppressLint("Range") int user_id=cursor.getInt(cursor.getColumnIndex("_id"));
                    //遍历数组,对每个好友,执行插入操作
                    for(String friend:friends){
                        //qq
                        int qq_num=Integer.parseInt(friend);
                        //使用query方法来根据用户名查询好友的 id
                        Cursor cursor2=db.query("user_table", new String[]{"_id"}, "QQNum = ?", new String[]{String.valueOf(qq_num)}, null, null, null);
                        //判断Cursor对象是否为空,如果不为空,说明查询到了数据
                        if(cursor2!=null&&cursor2.getCount()>0){
                            //移动Cursor对象到第一条记录
                            cursor2.moveToFirst();
                            //获取好友的id
                            @SuppressLint("Range") int friend_id = cursor2.getInt(cursor2.getColumnIndex("_id"));
                            db.execSQL("INSERT INTO friend (user_id, friend_id) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM friend WHERE user_id = ? AND friend_id = ?)", new Object[]{user_id, friend_id, user_id, friend_id});
                            //再次使用 ContentValues来组装数据
                            ContentValues values3=new ContentValues();
                            //将好友的id 和当前用户的id分别作为 user_id和friend_id插入到friend 表中，如果不存在重复的记录
                            db.execSQL("INSERT INTO friend (user_id, friend_id) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM friend WHERE user_id = ? AND friend_id = ?)", new Object[]{friend_id, user_id, friend_id, user_id});
                        }
                        cursor2.close();
                    }
                } while(cursor.moveToNext());
                //设置事务成功
                db.setTransactionSuccessful();
            }catch(Exception e){
                //打印异常信息
                e.printStackTrace();
            }finally{
                //结束事务
                db.endTransaction();
            }
        }
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int ii, int i1) {

        }

     //定义一个根据用户id和好友id来获取好友的详细信息的方法
    public Friend getFriendById(int user_id, int friend_id) {
        //创建一个好友对象，用于存储好友信息
        Friend friend = null;
        //获取一个可读数据库对象
        SQLiteDatabase db = UserDbHelper.getInstance(mContext).getReadableDatabase();
        //使用Cursor对象接收查询结果
        Cursor cursor = null;
        try {
            //使用query方法来查询friend_table表中的好友信息
            cursor = db.query("friend_table", null, "user_id=? and friend_id=?", new String[]{String.valueOf(user_id), String.valueOf(friend_id)}, null, null, null); // 修改了这里
            Log.d("UserDbHelper","cursor is : "+cursor);
            //如果查询到了好友信息，就将其封装为一个Friend对象
            if (cursor.moveToFirst()) {
                // 从查询结果中获取好友的QQ号码
                @SuppressLint("Range") int QQNum = cursor.getInt(cursor.getColumnIndex("QQNum"));
                Log.d("UserDbHelper","QQNum************* is : "+QQNum);
                //从查询结果中获取好友的姓名
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("friend_name"));
                Log.d("UserDbHelper","name************** is : "+name);
                //从查询结果中获取好友的头像
                @SuppressLint("Range") byte[] avatar = cursor.getBlob(cursor.getColumnIndex("avatar"));
                //从查询结果中获取好友的在线状态
                @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));
                friend = new Friend(friend_id, QQNum, name, avatar, status);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //最后,关闭cursor和db对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        //返回好友对象
        return friend;
    }



    @SuppressLint("Range")
    public String getUsername(int qq){
        //获取一个可读的数据库对象
        SQLiteDatabase db=getReadableDatabase();
        //使用Cursor对象来接收查询结果
        Cursor cursor = null;
        String username="";
        try{
            //使用query方法来根据qq号码查询用户的用户名
            cursor=db.query("user_table", new String[]{"username"}, "QQNum = ?", new String[]{String.valueOf(qq)}, null, null, null);
            //判断Cursor对象是否为空，如果不为空，说明查询到了数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动Cursor对象到第一条记录
                cursor.moveToFirst();
                //获取用户的id
                username=cursor.getString(cursor.getColumnIndex("username"));
            }
        }catch(Exception e){
            //捕获异常，打印错误信息
            e.printStackTrace();
        }finally{
            //最后,关闭 cursor对象和 db 对象
            if(cursor!=null){
                cursor.close();
            }
            db.close();
        }
        return username;
    }
    @SuppressLint("Range")
    public int getUser_id(int qq){
        //根据qq号码查询用户id
        //获取一个可读的数据库对象
        SQLiteDatabase db=getReadableDatabase();
        //使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        int user_id=0;
        try{
            //使用 query 方法来根据用户名查询用户的id
            cursor=db.query("user_table", new String[]{"_id"}, "QQNum = ?", new String[]{String.valueOf(qq)}, null, null, null);
            //判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动 Cursor 对象到第一条记录
                cursor.moveToFirst();
                //获取用户的 id
                user_id= cursor.getInt(cursor.getColumnIndex("_id"));
            }
        }catch(Exception e){
            //捕获异常，打印错误信息
            e.printStackTrace();
        }finally{
            //最后，关闭 cursor 对象和 db 对象
            if(cursor!=null){
                cursor.close();
            }
            db.close();
        }
        return user_id;
    }
    // 定义一个从数据库中获取好友列表的方法
    public List<Friend> getFriendList(int user_id) {
        // 创建一个好友列表，用于存储好友数据
        List<Friend> friendList = new ArrayList<>();
        // 获取一个可读数据库对象
        SQLiteDatabase db = UserDbHelper.getInstance(mContext).getReadableDatabase();
        // 使用Cursor对象接收查询结果
        Cursor cursor = null;
        try {
            // 使用query方法来查询friend表中的所有好友关系
            cursor = db.query("friend", null, "user_id=?", new String[]{String.valueOf(user_id)}, null, null, null);
            // 遍历查询结果，根据好友id来获取好友的详细信息
            while (cursor.moveToNext()) {
                // 获取好友的id
                @SuppressLint("Range") int friend_id = cursor.getInt(cursor.getColumnIndex("friend_id"));
                Log.d("UserDbHelper","friend_id_in_udb is : "+friend_id);
                // 调用数据库中的getFriendById方法，根据用户id和好友id来获取好友的详细信息

                Friend friend = getFriendById(user_id, friend_id); // 修改了这里
                Log.d("UserDbHelper","friend_in_udb is : "+friend_id);

                // 如果查询到了好友信息，就将其添加到好友列表中
                if (friend != null) {
                    friendList.add(friend);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 最后，关闭cursor和db对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        // 返回好友列表
        return friendList;
    }

   //这个方法先留存着,以后功能补充

  /*  // 定义一个辅助方法来获取好友列表的数据
    public List<User> getFriendList(int user_id) {
        // 创建一个 List<User> 类型的对象，用来存储好友列表的数据
        List<User> friendList = new ArrayList<>();
        // 获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        // 使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        try {
            // 使用 join 语句来从 user_table 表和 friend 表中联合查询好友的信息，使用 distinct 关键字来去除重复的记录
            cursor = db.rawQuery("select distinct u._id, u.username, u.avatar, u.signature from user_table u join friend f on u._id = f.friend_id where f.user_id = ?", new String[]{String.valueOf(user_id)});
            // 判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                // 移动 Cursor 对象到第一条记录
                cursor.moveToFirst();
                // 遍历 Cursor 对象，取出每个好友的信息
                do {
                    // 获取好友的 id，用户名，头像，和签名
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    @SuppressLint("Range") int qq = Integer.parseInt(cursor.getString(cursor.getColumnIndex("QQNum")));
                    @SuppressLint("Range") byte[] avatar = cursor.getBlob(cursor.getColumnIndex("avatar"));
                    @SuppressLint("Range") String motto = cursor.getString(cursor.getColumnIndex("signature"));
                    // 创建一个 User 对象，用来封装好友的信息
                    User friend = new User(qq,avatar,motto,id);
                    // 将 User 对象添加到 List<User> 对象中
                    friendList.add(friend);
                } while (cursor.moveToNext()); // 移动 Cursor 对象到下一条记录
            }
        } catch (Exception e) {
            // 捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            // 最后，关闭 cursor 对象和 db 对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        // 返回 List<User> 对象
        return friendList;
    }
*/

    //注册用户,参数是用户名和密码
    public int register(int qq,String password){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //创建一个ContentValues对象,用于存储要插入的数据
        ContentValues values=new ContentValues();
        //填充占位符,将用户名,密码分别放入values对象中
        values.put("QQNum",qq);
        values.put("password",password);
        values.put("remember_password",0); //默认不记住密码
        //定义一个nullColumnHack字符串,用于指定当values对象为空时,插入的默认值
        String nullColumnHack="values(null,?,?,?)";
        //执行插入操作,将values对象中的数据插入到user_table表中,并返回插入的行数
        int insert=(int)db.insert("user_table",nullColumnHack,values);
        add_init_friend(db);
        //关闭数据库连接
        db.close();
        //返回插入的行数,用于判断插入是否成功
        return insert;
    }

    //登录,根据qq查找用户,参数是qq
    @SuppressLint("Range")
    public UserInfo Login(int qq) {
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db = getWritableDatabase();
        //声明一个UserInfo对象,用于存储查询到的用户信息
        UserInfo userInfo = null;
        //定义一个SQL语句,用于根据qq号码查询用户表中的数据
        String sql = "select _id,QQNum,password,remember_password from user_table where QQNum=?;";//_id是自动分配的唯一标识符
        //定义一个字符串数组,用于存储查询条件
        String[] conditions = {String.valueOf(qq)};
        //执行查询操作,返回一个Cursor对象,用于遍历查询结果
        Cursor cursor = db.rawQuery(sql, conditions);
        //如果cursor有下一条信息,说明查询到了用户信息
        if (cursor.moveToNext()) {
            //获取cursor中各个字段的值,并赋值给相应的变量
            int _id = cursor.getInt(cursor.getColumnIndex("_id"));
            int qq_num = Integer.parseInt(cursor.getString(cursor.getColumnIndex("QQNum")));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            int remember_password = cursor.getInt(cursor.getColumnIndex("remember_password"));
            //创建一个UserInfo对象,并初始化其属性
            userInfo = new UserInfo(_id, qq_num, password,remember_password);
        }
        //关闭Cursor对象和数据库连接
        cursor.close();
        db.close();
        //返回userInfo对象
        return userInfo;
    }
    //更新传入的remember_password值
    public int updateRememberPassword(int qq,int remember_password){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //创建一个ContentValues对象,用于存储要更新的数据
        //ContentValues的对象可以使用put()方法来添加数据,传入两个参数
        //一个是键,表示列的名字,一个是值,表示列的数据
        ContentValues values=new ContentValues();
        //为remember_password字段赋值为参数传入的状态,0表示否,1表示是
        //put()方法用于向ContentValues对象中添加数据,传入两个参数
        //一个是键,表示列的名字,一个是值,表示列的数据
        values.put("remember_password",remember_password);
        //whereClause字符串的定义,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        //whereArgs字符串数组的定义,用于存储更新条件的值
        //whereArgs是{user_name},表示只有一个元素,就是user_name,表示要更新的用户名
        String[] whereArgs={String.valueOf(qq)};
        //执行更新操作,将ContentValues对象中的数据更新到user_table表中，并返回更新的行数
        int update=db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
        //返回更新的行数,用于判断更新是否成功
        return update;
    }
    @SuppressLint("Range")
    public String getUsername(Context context){
        //无参获取上下文
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储username字段的值
        String username="";
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,null,null,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取username字段的值
                username=cursor.getString(cursor.getColumnIndex("username"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回username字段的值,用于获取用户名
        return username;
    }

    @SuppressLint("Range")
//修改getUsername方法，增加一个参数，用于指定要查询的用户名
    public int getUserQQ(Context context, int qq){
        //查询user_table表中的所有记录，然后从中获取username字段的值，并返回
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //Cursor对象是一个接口,用于表示数据库查询的结果,指向查询结果中的一行数据
        //定义一个变量,用于存储username字段的值
        //String username="";
        int user_qq = 0;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            //查询user_table表中的指定用户名的记录，返回所有列的数据，不加任何其他条件，不进行分组，不进行过滤，不进行排序。
            //定义一个字符串数组,用于存储查询条件的值
            String[] conditions = {String.valueOf(qq)};
            cursor=db.query("user_table",null,"QQNum=?",conditions,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取username字段的值
                user_qq= Integer.parseInt(cursor.getString(cursor.getColumnIndex("QQNum")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回username字段的值,用于获取用户名
        return user_qq;
    }

    @SuppressLint("Range")
    public int getUserQQ(Context context){
        //无参获取上下文
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储username字段的值
        int user_qq=0;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,null,null,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取username字段的值
                user_qq= Integer.parseInt(cursor.getString(cursor.getColumnIndex("QQNum")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回username字段的值,用于获取用户名
        return user_qq;
    }

    @SuppressLint("Range")
    public String getPassword(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储password字段的值
        String password="";
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取password字段的值
                password=cursor.getString(cursor.getColumnIndex("password"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回password字段的值,用于获取密码
        return password;
    }
    @SuppressLint("Range")
    public int getRememberPassword(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储remember_password字段的值
        int remember_password=0;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据,Cursor对象的初始位置是在第一条数据之前
                cursor.moveToFirst();
                //获取remember_password字段的值
                //getInt()方法，用于获取Cursor对象中的数据的整数值,参数表示列的位置
                //getColunmIndex()方法，传入字段的名字，返回字段的索引
                remember_password=cursor.getInt(cursor.getColumnIndex("remember_password"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回remember_password字段的值,用于判断是否记住密码
        return remember_password;
    }
    //定义一个方法，用于根据用户名查询头像属性的值
    @SuppressLint("Range")
    public byte[] getAvatar(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储avatar字段的值
        byte[] avatar=null;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取avatar字段的值
                avatar=cursor.getBlob(cursor.getColumnIndex("avatar"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回avatar字段的值,用于获取头像
        return avatar;
    }

    //定义一个方法，用于根据用户名查询性别属性的值
    @SuppressLint("Range")
    public String getGender(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储gender字段的值
        String gender="";
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取gender字段的值
                gender=cursor.getString(cursor.getColumnIndex("gender"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回gender字段的值,用于获取性别
        return gender;
    }

    //定义一个方法，用于根据用户名查询生日属性的值
    @SuppressLint("Range")
    public String getBirthday(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储birthday字段的值
        String birthday="";
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取birthday字段的值
                birthday=cursor.getString(cursor.getColumnIndex("birthday"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回birthday字段的值,用于获取生日
        return birthday;
    }

    //定义一个方法，用于根据用户名查询地区属性的值
    @SuppressLint("Range")
    public String getRegion(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储region字段的值
        String region="";
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取region字段的值
                region=cursor.getString(cursor.getColumnIndex("region"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回region字段的值,用于获取地区
        return region;
    }

    @SuppressLint("Range")
    public int getYear(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        int year = 0;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取region字段的值
                year=cursor.getInt(cursor.getColumnIndex("year"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }

        return year;
    }


    @SuppressLint("Range")
    public int getMonth(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储region字段的值
        int month = 0;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取region字段的值
                month=cursor.getInt(cursor.getColumnIndex("month"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }

        return month;
    }



    @SuppressLint("Range")
    public int getDay(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储region字段的值
        int day = 0;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取region字段的值
                day=cursor.getInt(cursor.getColumnIndex("day"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }

        return day;
    }



    //定义一个方法，用于根据用户名查询签名属性的值
    @SuppressLint("Range")
    public String getSignature(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储signature字段的值
        String signature="";
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据
                cursor.moveToFirst();
                //获取signature字段的值
                signature=cursor.getString(cursor.getColumnIndex("signature"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        //返回signature字段的值,用于获取签名
        return signature;
    }
    public void updatePassword(int user_qq, String new_password){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        values.put("password",new_password);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个方法，用于根据用户名和新的头像值更新头像属性的值
    public void updateAvatar(int user_qq, byte[] new_avatar){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的头像值添加到ContentValues对象中
        values.put("avatar",new_avatar);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个方法，用于根据用户名和新的头像值更新friend_table头像属性的值
    public void updateAvatar_in_friend_table(int user_qq, byte[] new_avatar){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的头像值添加到ContentValues对象中
        values.put("avatar",new_avatar);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("friend_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个方法，用于根据用户名和新的性别值更新性别属性的值
    public void updateGender(int user_qq, String new_gender){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的性别值添加到ContentValues对象中
        values.put("gender",new_gender);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个方法，用于根据用户名和新的生日值更新生日属性的值
    public void updateBirthday(String user_name, String new_birthday){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="username=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={user_name};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的生日值添加到ContentValues对象中
        values.put("birthday",new_birthday);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }
    //定义一个方法，用于根据用户名和新的地区值更新地区属性的值
    public void updateRegion(int user_qq, String new_region){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("region",new_region);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }


    //更新年
    public void updateYear(int user_qq, int new_year){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("year",new_year);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }

    //更新月
    public void updateMonth(int user_qq, int new_month){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("month",new_month);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }

    //更新年
    public void updateDay(int user_qq, int new_day){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("day",new_day);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }



    //定义一个方法，用于根据qq和新的签名值更新签名属性的值
    public void updateSignature(int qq, String new_signature){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的签名值添加到ContentValues对象中
        values.put("signature",new_signature);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }

    public void updateQuestion1(int user_qq, String question1){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("question1",question1);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }
    public void updateAnswer1(int user_qq, String answer1){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("question_answer1",answer1);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }
    public void updateQuestion2(int user_qq, String question2){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("question2",question2);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }
    public void updateAnswer2(int user_qq, String answer2){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //将新的地区值添加到ContentValues对象中
        values.put("question_answer2",answer2);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }
    public void updateUsername(int user_qq, String username){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //添加到ContentValues对象中
        values.put("username",username);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("user_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }
    public void updateUsername_in_friend_table(int user_qq, String username){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getWritableDatabase();
        //定义一个whereClause字符串,用于指定更新条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储更新条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个ContentValues对象,用于存储更新的列和值
        ContentValues values=new ContentValues();
        //添加到ContentValues对象中
        values.put("friend_name",username);
        //执行更新操作,将ContentValues对象中的值更新到数据库中
        db.update("friend_table",values,whereClause,whereArgs);
        //关闭数据库连接
        db.close();
    }
    //获取密保1
    @SuppressLint("Range")
    public String getQuestion1(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储question1字段的值
        String question1=null;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据,Cursor对象的初始位置是在第一条数据之前
                cursor.moveToFirst();
                //获取question1字段的值
                //getInt()方法，用于获取Cursor对象中的数据的整数值,参数表示列的位置
                //getColunmIndex()方法，传入字段的名字，返回字段的索引
                question1= cursor.getString(cursor.getColumnIndex("question1"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        return question1;
    }
    //获取密保2
    @SuppressLint("Range")
    public String getQuestion2(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储question2字段的值
        String question2=null;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据,Cursor对象的初始位置是在第一条数据之前
                cursor.moveToFirst();
                //获取question2字段的值
                //getInt()方法，用于获取Cursor对象中的数据的整数值,参数表示列的位置
                //getColunmIndex()方法，传入字段的名字，返回字段的索引
                question2= cursor.getString(cursor.getColumnIndex("question2"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        return question2;
    }
    //获取密保1答案
    @SuppressLint("Range")
    public String getAnswer1(int user_qq){
        //获取SQLiteDatabase实例,用于操作数据库
        SQLiteDatabase db=getReadableDatabase();
        //定义一个whereClause字符串,用于指定查询条件
        String whereClause="QQNum=?";
        //定义一个whereArgs字符串数组,用于存储查询条件的值
        String[] whereArgs={String.valueOf(user_qq)};
        //定义一个Cursor对象,用于接收查询结果
        Cursor cursor=null;
        //定义一个变量,用于存储question_answer1字段的值
        String answer1=null;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);
            //如果Cursor对象不为空,并且有数据
            if(cursor!=null&&cursor.getCount()>0){
                //移动到第一条数据,Cursor对象的初始位置是在第一条数据之前
                cursor.moveToFirst();
                //获取question_answer1字段的值
                //getInt()方法，用于获取Cursor对象中的数据的整数值,参数表示列的位置
                //getColunmIndex()方法，传入字段的名字，返回字段的索引
                answer1= cursor.getString(cursor.getColumnIndex("question_answer1"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        return answer1;
    }
    //获取密保2答案
    @SuppressLint("Range")
    public String getAnswer2(int user_qq){
        SQLiteDatabase db=getReadableDatabase();
        String whereClause="QQNum=?";
        String[] whereArgs={String.valueOf(user_qq)};
        Cursor cursor=null;
        //定义一个变量,用于存储question_answer2字段的值
        String answer2=null;
        try{
            //执行查询操作,将查询结果赋值给Cursor对象
            cursor=db.query("user_table",null,whereClause,whereArgs,null,null,null);

            if(cursor!=null&&cursor.getCount()>0){

                cursor.moveToFirst();
                //获取question_answer2字段的值
                //getColunmIndex()方法，传入字段的名字，返回字段的索引
                answer2= cursor.getString(cursor.getColumnIndex("question_answer2"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            //关闭Cursor对象
            if(cursor!=null){
                cursor.close();
            }
            //关闭数据库连接
            db.close();
        }
        return answer2;
    }

    //定义一个getAllFriends()方法,用于返回一个好友列表
    public List<Friend>getAllFriends(int userId) {
        //创建一个空的好友列表
        List<Friend> friendList = new ArrayList<>();
        //创建一个数据库连接
        SQLiteDatabase db = getReadableDatabase();
        //根据用户的id,从friend表中查询出所有的好友id
        Cursor cursor1 = db.query("friend", new String[]{"friend_id"}, "user_id = ?", new String[]{String.valueOf(userId)}, null, null, null);
        //遍历查询结果
        while (cursor1.moveToNext()) {
            //获取好友id
            @SuppressLint("Range") int friendId = cursor1.getInt(cursor1.getColumnIndex("friend_id"));
            //根据好友id,从friend_table表中查询出所有的好友信息
            Cursor cursor2 = db.query("friend_table", null, "user_id = ? and friend_id = ?", new String[]{String.valueOf(userId), String.valueOf(friendId)}, null, null, null);
            //如果查询到好友信息
            if (cursor2.moveToFirst()) {
                //创建一个Friend对象,用于封装好友信息
                Friend friend = new Friend();
                //获取好友qq号
                @SuppressLint("Range") int qqNum = cursor2.getInt(cursor2.getColumnIndex("QQNum"));
                //获取好友用户名
                @SuppressLint("Range") String friendName = cursor2.getString(cursor2.getColumnIndex("friend_name"));
                Log.d("UserDbHelper","friendName_in ud is : "+friendName);
                //获取好友头像
                @SuppressLint("Range") byte[] avatar = cursor2.getBlob(cursor2.getColumnIndex("avatar"));
                //获取好友在线状态
                @SuppressLint("Range") String status = cursor2.getString(cursor2.getColumnIndex("status"));
                //将好友信息设置到Friend对象中
                friend.setId(friendId);
                friend.setQQNum(qqNum);
                friend.setName(friendName);
                friend.setAvatar(avatar);
                friend.setStatus(status);
                // 将Friend对象添加到好友列表中
                friendList.add(friend);
            }
            //关闭第二个游标
            cursor2.close();
        }
        //关闭第一个游标
        cursor1.close();
        //关闭数据库连接
        db.close();
        //返回好友列表
        return friendList;
    }


    //这一部分是和自己发表的说说内容关联的
    //insert方法，用来插入动态的内容和图片

    //                "QQNum integer," +  //动态的发布者的 QQ 号，类型为整型，外键，引用 user_table 的 QQNum
    //                "content text," + //动态的内容，类型为文本
    //                "image BLOB," + //动态的图片，类型为 BLOB
    //                "like_count integer," + //动态的点赞数，类型为整型
    //                "comment_count integer," + //动态的评论数，类型为整型
    //                "is_like boolean" + //动态是否被当前用户点赞，类型为布尔，表示是或否
    //                ")");


    //定义一个insertSaying方法，用来插入动态的内容和图片
    public int insertSaying(int qq, String content, byte[] image) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //使用ContentValues对象来存储要插入的列和值
        ContentValues values = new ContentValues();
        values.put("QQNum", qq); //插入动态的qq号
        values.put("content", content); //插入动态的内容
        values.put("image", image); //插入动态的图片
        //使用SQLiteDatabase的insert方法来执行插入操作，返回插入的动态的ID
        int id = (int) db.insert("dongtai_table", null, values);
        //关闭数据库对象
        db.close();
        //返回插入的动态的ID
        return id;
    }
    //定义一个getDongtaiList方法，用来获取动态的列表
    public List<Dongtai> getDongtaiList() {
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个dongtaiList集合，用来存储动态的对象
        List<Dongtai> dongtaiList = new ArrayList<>();
        //使用Cursor对象来接收查询结果
        Cursor cursor = null;
        try {
            //使用query方法来查询所有的动态
            cursor = db.query("dongtai_table", null, null, null, null, null, null);
            //判断Cursor对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                //使用while循环，遍历Cursor对象中的每一条记录
                while (cursor.moveToNext()) {
                    //获取动态的ID
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                    //获取动态的qq号
                    @SuppressLint("Range") int qq = cursor.getInt(cursor.getColumnIndex("QQNum"));
                    //使用getMainName方法，根据qq号获取用户名
                    String main_name = getUsername(qq);
                    //获取动态的内容
                    @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                    //使用getAvatar方法，根据qq号获取头像
                    byte[] avatar = getAvatar(qq);
                    //获取动态的图片
                    @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex("image"));
                    //获取动态的点赞数
                    @SuppressLint("Range") int like = cursor.getInt(cursor.getColumnIndex("like_count"));
                    //获取动态的评论数
                    @SuppressLint("Range") int comment = cursor.getInt(cursor.getColumnIndex("comment_count"));
                    //获取动态是否被当前用户点赞
                    @SuppressLint("Range") boolean is_like = cursor.getInt(cursor.getColumnIndex("is_like")) == 1 ? true : false;
                    //判断动态是否有图片，设置hasPicture属性的值
                    boolean hasPicture;
                    if (image == null) {
                        hasPicture = false;
                    } else {
                        hasPicture = true;
                    }
                    //将动态的各种属性传递给Dongtai对象的构造方法，创建一个Dongtai对象
                    Dongtai dongtai = new Dongtai(id, main_name, content, avatar, image, like, comment, hasPicture, is_like);
                    //将Dongtai对象添加到dongtaiList集合中
                    dongtaiList.add(dongtai);
                }
            }
        } catch (Exception e) {
            //捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            //最后，关闭cursor对象和db对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        //返回dongtaiList集合
        return dongtaiList;
    }

    //这里开始是和动态部分相关联的

    //定义一个 updateContent 方法，用来更新动态的内容
    public void updateContent(int dongtai_id, String content) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 whereClause 字符串，用于指定更新条件
        String whereClause = "_id = ?";
        //定义一个 whereArgs 字符串数组，用于存储更新条件的值
        String[] whereArgs = {String.valueOf(dongtai_id)};
        //定义一个 ContentValues 对象，用于存储更新的列和值
        ContentValues values = new ContentValues();
        //将新的内容值添加到 ContentValues 对象中
        values.put("content", content);
        //执行更新操作，将 ContentValues 对象中的值更新到数据库中
        db.update("dongtai_table", values, whereClause, whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个 updateImage 方法，用来更新动态的图片
    public void updateImage(int dongtai_id, byte[] image) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 whereClause 字符串，用于指定更新条件
        String whereClause = "_id = ?";
        //定义一个 whereArgs 字符串数组，用于存储更新条件的值
        String[] whereArgs = {String.valueOf(dongtai_id)};
        //定义一个 ContentValues 对象，用于存储更新的列和值
        ContentValues values = new ContentValues();
        //将新的图片值添加到 ContentValues 对象中
        values.put("image", image);
        //执行更新操作，将 ContentValues 对象中的值更新到数据库中
        db.update("dongtai_table", values, whereClause, whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个 updateLikeCount 方法，用来更新动态的点赞数
    public void updateLikeCount(int dongtai_id, int like_count) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 whereClause 字符串，用于指定更新条件
        String whereClause = "_id = ?";
        //定义一个 whereArgs 字符串数组，用于存储更新条件的值
        String[] whereArgs = {String.valueOf(dongtai_id)};
        //定义一个 ContentValues 对象，用于存储更新的列和值
        ContentValues values = new ContentValues();
        //将新的点赞数值添加到 ContentValues 对象中
        values.put("like_count", like_count);
        //执行更新操作，将 ContentValues 对象中的值更新到数据库中
        db.update("dongtai_table", values, whereClause, whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个 updateCommentCount 方法，用来更新动态的评论数
    public void updateCommentCount(int dongtai_id, int comment_count) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 whereClause 字符串，用于指定更新条件
        String whereClause = "_id = ?";
        //定义一个 whereArgs 字符串数组，用于存储更新条件的值
        String[] whereArgs = {String.valueOf(dongtai_id)};
        //定义一个 ContentValues 对象，用于存储更新的列和值
        ContentValues values = new ContentValues();
        //将新的评论数值添加到 ContentValues 对象中
        values.put("comment_count", comment_count);
        //执行更新操作，将 ContentValues 对象中的值更新到数据库中
        db.update("dongtai_table", values, whereClause, whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个 updateIsLike 方法，用来更新动态是否被当前用户点赞
    public void updateIsLike(int dongtai_id, int is_like) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 whereClause 字符串，用于指定更新条件
        String whereClause = "_id = ?";
        //定义一个 whereArgs 字符串数组，用于存储更新条件的值
        String[] whereArgs = {String.valueOf(dongtai_id)};
        //定义一个 ContentValues 对象，用于存储更新的列和值
        ContentValues values = new ContentValues();
        //将新的是否点赞值添加到 ContentValues 对象中
        values.put("is_like", is_like);
        //执行更新操作，将 ContentValues 对象中的值更新到数据库中
        db.update("dongtai_table", values, whereClause, whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个 getContent 方法，用来获取动态的内容
    @SuppressLint("Range")
    public String getContent(int dongtai_id) {
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个 content 字符串，用于存储动态的内容
        String content = null;
        //使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        try {
            //使用 query 方法来根据动态的 ID 查询动态的内容
            cursor = db.query("dongtai_table", new String[]{"content"}, "_id = ?", new String[]{String.valueOf(dongtai_id)}, null, null, null);
            //判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                //移动 Cursor 对象到第一条记录
                cursor.moveToFirst();
                //获取动态的内容
                content = cursor.getString(cursor.getColumnIndex("content"));
            }
        } catch (Exception e) {
            //捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            //最后，关闭 cursor 对象和 db 对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return content;
    }

    //定义一个 getImage 方法，用来获取动态的图片
    @SuppressLint("Range")
    public byte[] getImage(int dongtai_id) {
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个 image 字节数组，用于存储动态的图片
        byte[] image = null;
        //使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        try {
            //使用 query 方法来根据动态的 ID 查询动态的图片
            cursor = db.query("dongtai_table", new String[]{"image"}, "_id = ?", new String[]{String.valueOf(dongtai_id)}, null, null, null);
            //判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                //移动 Cursor 对象到第一条记录
                cursor.moveToFirst();
                //获取动态的图片
                image = cursor.getBlob(cursor.getColumnIndex("image"));
            }
        } catch (Exception e) {
            //捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            //最后，关闭 cursor 对象和 db 对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return image;
    }

    //定义一个 getLikeCount 方法，用来获取动态的点赞数
    @SuppressLint("Range")
    public int getLikeCount(int dongtai_id) {
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个 like_count 整型，用于存储动态的点赞数
        int like_count = 0;
        //使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        try {
            //使用 query 方法来根据动态的 ID 查询动态的点赞数
            cursor = db.query("dongtai_table", new String[]{"like_count"}, "_id = ?", new String[]{String.valueOf(dongtai_id)}, null, null, null);
            //判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                //移动 Cursor 对象到第一条记录
                cursor.moveToNext();
                //获取动态的点赞数
                like_count = cursor.getInt(cursor.getColumnIndex("like_count"));
            }
        } catch (Exception e) {
            //捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            //最后，关闭 cursor 对象和 db 对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return like_count;
    }

    //定义一个 getCommentCount 方法，用来获取动态的评论数
    @SuppressLint("Range")
    public int getCommentCount(int dongtai_id) {
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个 comment_count 整型，用于存储动态的评论数
        int comment_count = 0;
        //使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        try {
            //使用 query 方法来根据动态的 ID 查询动态的评论数
            cursor = db.query("dongtai_table", new String[]{"comment_count"}, "_id = ?", new String[]{String.valueOf(dongtai_id)}, null, null, null);
            //判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                //移动 Cursor 对象到第一条记录
                cursor.moveToFirst();
                //获取动态的评论数
                comment_count = cursor.getInt(cursor.getColumnIndex("comment_count"));
            }
        } catch (Exception e) {
            //捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            //最后，关闭 cursor 对象和 db 对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return comment_count;
    }


    //定义一个 getIsLike 方法，用来获取动态是否被当前用户点赞
    @SuppressLint("Range")
    public boolean getIsLike(int dongtai_id) {
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个 is_like 布尔型，用于存储动态是否被当前用户点赞
        boolean is_like = false;
        //使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        try {
            //使用 query 方法来根据动态的 ID 查询动态是否被当前用户点赞
            cursor = db.query("dongtai_table", new String[]{"is_like"}, "_id = ?", new String[]{String.valueOf(dongtai_id)}, null, null, null);
            //判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                //移动 Cursor 对象到第一条记录
                cursor.moveToFirst();
                //获取动态是否被当前用户点赞
                is_like = cursor.getInt(cursor.getColumnIndex("is_like"))==1?true:false;
            }
        } catch (Exception e) {
            //捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            //最后，关闭 cursor 对象和 db 对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return is_like;
    }

    //评论部分
    //定义一个 insertComment 方法，用来插入一条新的评论
    public void insertComment(int dongtai_id, int user_id, String content) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 ContentValues 对象，用于存储插入的列和值
        ContentValues values = new ContentValues();
        //将动态的 ID，用户的 ID，评论的内容添加到 ContentValues 对象中
        values.put("dongtai_id", dongtai_id);
        values.put("user_id", user_id);
        values.put("content", content);
        //执行插入操作，将 ContentValues 对象中的值插入到数据库中
        db.insert("comment_table", null, values);
        //关闭数据库连接
        db.close();
    }

    //定义一个 insertComment 方法，用来插入一条新的评论
    public void insertComment(Comment comment) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 ContentValues 对象，用于存储插入的列和值
        ContentValues values = new ContentValues();
        //将评论的动态的 ID，用户的 ID，评论的内容和时间添加到 ContentValues 对象中
        values.put("dongtai_id", comment.getDongtai_id());
        values.put("user_id", comment.getUser_id());
        values.put("content", comment.getContent());
        //执行插入操作，将 ContentValues 对象中的值插入到数据库中
        db.insert("comment_table", null, values);
        //关闭数据库连接
        db.close();
    }

    //定义一个 getCommentsByDongtaiId 方法,用来根据动态id查询对应的评论数据
    public List<Comment> getCommentsByDongtaiId(int dongtai_id) {
        //创建一个存储评论的集合
        List<Comment> commentList = new ArrayList<>();
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个查询条件，根据动态id过滤评论
        String selection = "dongtai_id = ?";
        //定义一个查询参数，根据动态id的值赋值
        String[] selectionArgs = new String[] {String.valueOf(dongtai_id)};
        //执行查询操作，返回一个Cursor对象
        Cursor cursor = db.query("comment_table", null, selection, selectionArgs, null, null, null);
        //判断Cursor对象是否为空
        if (cursor != null) {
            //遍历Cursor对象，获取每一条评论的数据
            while (cursor.moveToNext()) {
                //获取评论的id,用户的id(qq),评论的内容和时间
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("_id"));
                Log.d("UserDbHelper","id_ud is : "+id);
                @SuppressLint("Range") int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
                Log.d("UserDbHelper","user_id_ud is : "+user_id);
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                //创建一个Comment对象，用于封装评论的数据
                Comment comment = new Comment(id, dongtai_id, user_id, content);
                //将Comment对象添加到评论集合中
                commentList.add(comment);
            }
            //关闭Cursor对象
            cursor.close();
        }
        //关闭数据库连接
        db.close();
        //返回评论集合
        return commentList;
    }

    //定义一个 deleteComment 方法，用来删除一条评论
    public void deleteComment(int comment_id) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 whereClause 字符串，用于指定删除条件
        String whereClause = "_id = ?";
        //定义一个 whereArgs 字符串数组，用于存储删除条件的值
        String[] whereArgs = {String.valueOf(comment_id)};
        //执行删除操作，将数据库中符合条件的记录删除
        db.delete("comment_table", whereClause, whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个 updateComment 方法，用来更新一条评论的内容
    public void updateComment(int comment_id, String content) {
        //获取一个可写的数据库对象
        SQLiteDatabase db = getWritableDatabase();
        //定义一个 whereClause 字符串，用于指定更新条件
        String whereClause = "_id = ?";
        //定义一个 whereArgs 字符串数组，用于存储更新条件的值
        String[] whereArgs = {String.valueOf(comment_id)};
        //定义一个 ContentValues 对象，用于存储更新的列和值
        ContentValues values = new ContentValues();
        //将新的评论内容添加到 ContentValues 对象中
        values.put("content", content);
        //执行更新操作，将 ContentValues 对象中的值更新到数据库中
        db.update("comment_table", values, whereClause, whereArgs);
        //关闭数据库连接
        db.close();
    }

    //定义一个 getComment 方法，用来获取一条评论的信息
    public Comment getComment(int comment_id) {
        //获取一个可读的数据库对象
        SQLiteDatabase db = getReadableDatabase();
        //定义一个 comment 对象，用于存储评论的信息
        Comment comment = null;
        //使用 Cursor 对象来接收查询结果
        Cursor cursor = null;
        try {
            //使用 query 方法来根据评论的 ID 查询评论的信息
            cursor = db.query("comment_table", null, "_id = ?", new String[]{String.valueOf(comment_id)}, null, null, null);
            //判断 Cursor 对象是否为空，如果不为空，说明查询到了数据
            if (cursor != null && cursor.getCount() > 0) {
                //移动 Cursor 对象到第一条记录
                cursor.moveToFirst();
                //获取评论的信息
                @SuppressLint("Range") int dongtai_id = cursor.getInt(cursor.getColumnIndex("dongtai_id"));
                @SuppressLint("Range") int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                //将评论的信息封装成 Comment 对象
                comment = new Comment(comment_id, dongtai_id, user_id, content);
            }
        } catch (Exception e) {
            //捕获异常，打印错误信息
            e.printStackTrace();
        } finally {
            //最后，关闭 cursor 对象和 db 对象
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return comment;
    }

}


