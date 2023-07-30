package com.example.bt.ViewModel;

public class Khoa {
    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String Name;
    private String id;
    public Khoa() {
        // Default constructor required for Firestore
    }

    public Khoa(String id, String name) {
        this.id = id;
        this.Name = name;
    }

}
