package com.punbook.mayankgupta.havi.dummy;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mayankgupta on 12/03/17.
 */

public class Task implements Serializable{

    //public static final String SUMMARY_KEY = "summary";
    public static final String STATUS_KEY = "status";
    public static final String COMMENTS_KEY = "comments";
    public static final String NAME_KEY = "name";

    private  String id;
    private String name;
    private  String summary;
    private  String comments;
    private Long startDate;
    private Long expiryDate;
    private String postKey;
    private Status status = Status.ACTIVE;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    /*public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }*/

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Long getStartDate() {
        return startDate;
    }

    public void setStartDate(Long startDate) {
        this.startDate = startDate;
    }

    public Long getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Long expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



   /* @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", getId());
        result.put("status", getStatus());
        result.put("summary", getSummary());
        result.put("comments", getComments());
        result.put("startDate", getStartDate());
        result.put("expiryDate", getExpiryDate());
        result.put("postKey",getPostKey());

        return result;
    }*/

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", summary='" + summary + '\'' +
                ", comments='" + comments + '\'' +
                ", startDate=" + startDate +
                ", expiryDate=" + expiryDate +
                ", postKey='" + postKey + '\'' +
                ", status=" + status +
                '}';
    }
}
