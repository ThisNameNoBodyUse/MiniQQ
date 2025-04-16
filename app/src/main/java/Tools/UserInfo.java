package Tools;

public class UserInfo {
    //实体
    private int _id; //用户id,整数类型
    private int qq;
    private String username; //用户名,字符串类型
    private String password; //用户密码,字符串类型
    public static UserInfo sUserInfo; //用户的单例对象,是一个静态的UserInfo类型
    public int remember_password;//是否记住密码,为整型
    public static UserInfo getInstance(){
        return sUserInfo; //返回单例对象
    }
    public static void setUserInfo(UserInfo userInfo){
        sUserInfo=userInfo; //将参数赋值给单例对象
    }
    public UserInfo(int _id, int qq, String password, int remember_password){
        //创建用户对象,并初始化其属性
        this._id=_id;
        this.qq=qq;
        this.password=password;
        this.remember_password=remember_password;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getQq(){return qq;}
    public void setQq(int qq){this.qq=qq;}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public int getRemember_password(){
        return remember_password;
    }
    public void setRemember_password(int remember_password){
        this.remember_password=remember_password;
    }

}
