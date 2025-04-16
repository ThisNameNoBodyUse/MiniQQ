package Tools;

//这个是用于主界面下方第一个按钮的消息类
public class Message {

    private byte[] avatar;
    private String name;
    private String content;
    private String time;
    private int friend_id;//好友id

    public Message(byte[] avatar, String name, String content, String time,int friend_id) {
        this.avatar = avatar;
        this.name = name;
        this.content = content;
        this.time = time;
        this.friend_id=friend_id;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public int getFriend_id(){
        return friend_id;
    }
    public void setFriend_id(int friend_id){
        this.friend_id=friend_id;
    }
}