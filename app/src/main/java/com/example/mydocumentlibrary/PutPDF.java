package com.example.mydocumentlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.Serializable;

public class PutPDF implements Serializable {

    public String name;
    public String url;
    public String notes;
    public String createdDate;
    public String deadlineDate;
    public String originalDoc;
//    public int imageResource;
    public boolean permissionForFriends;

    private String fileType;

//    public String scannedImageURL;
    public PutPDF() {
    }


    public PutPDF(String name, String url, String notes, String originalDoc, String createdDate, String deadlineDate, boolean permissionForFriends) {
        this.name = name;
        this.url = url;
        this.notes = notes;
        this.originalDoc = originalDoc;
        this.createdDate = createdDate;
        this.deadlineDate = deadlineDate;
        this.permissionForFriends = permissionForFriends;
    }
    public PutPDF(String name, String url, String createdDate) {
        this.name = name;
        this.url = url;
        this.createdDate = createdDate;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getOriginalDoc() {
        return originalDoc;
    }

    public void setOriginalDoc(String originalDoc) {
        this.originalDoc = originalDoc;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(String deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

//    public int getImageResource() {
//        return imageResource;
//    }

    public boolean isPermissionForFriends() {
        return permissionForFriends;
    }

    public void setPermissionForFriends(boolean permissionForFriends) {
        this.permissionForFriends = permissionForFriends;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }



    //    public String getScannedImageURL() {
//        return scannedImageURL;
//    }
//
//    public void setScannedImageURL(String scannedImageURL) {
//        this.scannedImageURL = scannedImageURL;
//    }
}