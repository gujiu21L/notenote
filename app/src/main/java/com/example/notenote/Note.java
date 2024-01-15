package com.example.notenote;

public class Note {
    private long id ;
    private String content;
    private String time;
    public Note(){
    }
    public Note(String content,String time){
        this.content=content;
        this.time=time;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public void setTime(String time) {
        this.time = time;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }
}
