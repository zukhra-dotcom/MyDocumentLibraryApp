package com.example.mydocumentlibrary;

public class Friends {
    private String email, uid;

    public Friends(String email, String uid) {
        this.email = email;
        this.uid = uid;
    }

    public Friends() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
