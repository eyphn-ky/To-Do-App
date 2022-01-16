package com.eyphn.todolist.spinner;

public class SpinnerCategory {
    public String Id;
    public String Name;

    public SpinnerCategory(String id, String name) {
        this.Id = id;
        this.Name = name;
    }
    @Override
    public String toString() {
        return Name;
    }
}
