package com.example.bt.ViewModel;

public class Student {
    private int mssv;
    private String Name;
    private float DTB;
    private String khoa; // Add the "khoa" field

    public Student() {
    }

    public Student(int mssv, String name, float DTB, String khoa) {
        this.mssv = mssv;
        Name = name;
        this.DTB = DTB;
        this.khoa = khoa; // Initialize the "khoa" field in the constructor
    }

    public int getMssv() {
        return mssv;
    }

    public void setMssv(int mssv) {
        this.mssv = mssv;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public float getDTB() {
        return DTB;
    }

    public void setDTB(float DTB) {
        this.DTB = DTB;
    }

    public String getKhoa() {
        return khoa;
    }

    public void setKhoa(String khoa) {
        this.khoa = khoa;
    }
}
