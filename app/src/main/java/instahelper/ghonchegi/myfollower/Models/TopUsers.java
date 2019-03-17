package instahelper.ghonchegi.myfollower.Models;

public class TopUsers {

    private String userName;
    private String picUrl;
    private int id;
    private String count;
    private String type;
    private String prizeCount;


    public TopUsers() {
    }

    public TopUsers(String userName, String picUrl, int id, String count, String type, String prizeCount) {
        this.userName = userName;
        this.picUrl = picUrl;
        this.id = id;
        this.count = count;
        this.type = type;
        this.prizeCount = prizeCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrizeCount() {
        return prizeCount;
    }

    public void setPrizeCount(String prizeCount) {
        this.prizeCount = prizeCount;
    }
}
