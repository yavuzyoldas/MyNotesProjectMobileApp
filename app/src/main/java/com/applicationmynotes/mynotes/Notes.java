package com.applicationmynotes.mynotes;



public class Notes  {

    private String id;
    private String title;
    private String content;
    private String summary;
    private String date;

    public Notes(String id, String title, String content, String date) {

        this.id = id;
        this.title = title;
        this.summary = content.length() > 30 ? content.substring(0,28)+"..." : content;
        this.content = content;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
