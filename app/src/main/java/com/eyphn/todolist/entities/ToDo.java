package com.eyphn.todolist.entities;

import java.util.Date;

public class ToDo {
    private int Id;
    private String Detail;
    private String DueDate;
    private String DueTime;
    private int CategoryId;
    private int IsNotified;

    public ToDo() {
    }



    public ToDo(String detail, String dueDate, int categoryId, String dueTime, int isNotified) {
        Detail = detail;
        DueDate = dueDate;
        CategoryId = categoryId;
        DueTime = dueTime;
        IsNotified = isNotified;
    }
    public int getIsNotified() {
        return IsNotified;
    }
    public void setIsNotified(int notified) {
        IsNotified = notified;
    }

    public String getDueTime() {return DueTime;}

    public void setDueTime(String dueTime) {DueTime = dueTime;}


    public String getDetail() {
        return Detail;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }


    public int getId(){return Id;}

    public void setId(int id) {
        Id = id;
    }


    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }


    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(int categoryId) {
        CategoryId = categoryId;
    }
}
