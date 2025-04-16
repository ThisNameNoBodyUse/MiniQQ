package Tools;
public class Dongtai{
    //好友动态界面的实现逻辑
    private int id;//用来存储动态的ID
    private String name;
    private String content;
    private byte []avatar;
    private byte[]picture;
    private int like;
    private int comment;
    private boolean hasPicture; //是否有图片
    private boolean is_liked;//有无点赞


    public Dongtai(int id, String name, String content, byte[] avatar, byte[] picture, int like, int comment, boolean hasPicture,boolean is_liked) {
        this.id = id; //给id属性赋值
        this.name = name;
        this.content = content;
        this.avatar = avatar;
        this.picture = picture;
        this.like = like;
        this.comment = comment;
        this.hasPicture = hasPicture;
        this.is_liked=is_liked;
    }
    //用来获取动态ID
    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public byte[] getPicture() {
        return picture;
    }

    public int getLike() {
        return like;
    }

    public int getComment() {
        return comment;
    }

    public boolean isHasPicture() {
        return hasPicture;
    }

    public void setLike(int like) {
        this.like = like;
    }
    public boolean isLiked(){
        return is_liked;
    }
    public void set_liked(boolean is_liked){
        this.is_liked=is_liked;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }
}
