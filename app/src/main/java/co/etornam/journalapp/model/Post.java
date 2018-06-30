package co.etornam.journalapp.model;

public class Post {
    public String title;
    public String body;
    public String category;
    public long timeStamp;

    public Post(String title, String body, String category, long timeStamp) {
        this.title = title;
        this.body = body;
        this.category = category;
        this.timeStamp = timeStamp;
    }

    public Post() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
