package instahelper.ghonchegi.myfollower.Models;

public class Messages {

    private int id;
    private int status;
    private  String title;
    private String created_at;
    private int user_name;
    private String description;

    public Messages(int id, int status, String title, String created_at, int user_name, String description) {
        this.id = id;
        this.status = status;
        this.title = title;
        this.created_at = created_at;
        this.user_name = user_name;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUser_name() {
        return user_name;
    }

    public void setUser_name(int user_name) {
        this.user_name = user_name;
    }

    public Messages() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
}
