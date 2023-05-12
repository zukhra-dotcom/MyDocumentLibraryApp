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
    public int imageResource;
    public boolean hasDeleteButton;
    public boolean hasCreateDeadlineButton;

    public PutPDF() {
    }


    public PutPDF(String name, String url, String notes, String createdDate, String deadlineDate, int imageResource, boolean hasDeleteButton, boolean hasCreateDeadlineButton) {
        this.name = name;
        this.url = url;
        this.notes = notes;
        this.createdDate = createdDate;
        this.deadlineDate = deadlineDate;
        this.imageResource = imageResource;
        this.hasDeleteButton = hasDeleteButton;
        this.hasCreateDeadlineButton = hasCreateDeadlineButton;
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

    public int getImageResource() {
        return imageResource;
    }

    public boolean hasDeleteButton() {
        return hasDeleteButton;
    }

    public boolean hasCreateDeadlineButton() {
        return hasCreateDeadlineButton;
    }
}