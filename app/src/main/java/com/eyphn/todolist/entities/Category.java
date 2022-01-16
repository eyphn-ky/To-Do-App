package com.eyphn.todolist.entities;

public class Category {
    private int Id;
    private String Name;

    public Category(int id, String name) {
        Id = id;
        Name = name;
    }
    public Category() { }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
