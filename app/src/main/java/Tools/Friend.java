package Tools;

import java.io.Serializable;

//定义一个好友的数据类
public class Friend implements Serializable{
    private int id; //好友id
    private int QQNum; //好友QQ
    private String name; //好友的用户名
    private byte[] avatar; //好友的头像
    private String status; //好友的在线状态

    // 定义一个好友的构造方法
    public Friend(){}
    public Friend(int id, int QQNum, String name, byte[] avatar, String status) {
        this.id = id;
        this.QQNum = QQNum;
        this.name = name;
        this.avatar = avatar;
        this.status = status;
    }


    public int getId() {
        return id;
    }


    public int getQQNum() {
        return QQNum;
    }


    public String getName() {
        return name;
    }



    public byte[] getAvatar() {
        return avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setQQNum(int QQNum) {
        this.QQNum = QQNum;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

