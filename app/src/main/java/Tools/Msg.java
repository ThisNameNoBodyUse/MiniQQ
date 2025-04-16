package Tools;

//这个是进入聊天页面后的消息类
public class Msg {
   //消息类型和内容

    public static final int TYPE_RECEIVED = 0; //表示这是一条收到的消息
    public static final int TYPE_SENT = 1; //表示这是一条发送的消息

    private String content; //消息的内容
    private int type; //消息的类型
    private String time; //发送时间

    public Msg(String content, int type,String  time) {
        this.content = content;
        this.type = type;
        this.time=time;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }
    public String getTime(){return time;}

}

