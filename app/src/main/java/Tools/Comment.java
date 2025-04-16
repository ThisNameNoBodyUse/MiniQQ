package Tools;

//定义一个Comment类,用于封装评论的信息
public class Comment {
    //定义评论的属性
    private int comment_id; //评论的ID
    private int dongtai_id; //评论的动态的ID
    private int user_id; //评论的用户的qq
    private String content; //评论的内容

    //定义一个带参数的构造方法，用于创建Comment对象
    public Comment(int comment_id, int dongtai_id, int user_id, String content) {
        this.comment_id = comment_id;
        this.dongtai_id = dongtai_id;
        this.user_id = user_id;
        this.content = content;
    }

    public int getComment_id() {
        return comment_id;
    }

    public void setComment_id(int comment_id) {
        this.comment_id = comment_id;
    }

    public int getDongtai_id() {
        return dongtai_id;
    }

    public void setDongtai_id(int dongtai_id) {
        this.dongtai_id = dongtai_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

}

