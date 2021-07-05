package com.example.project.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "notes")
public class Note implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "Title")
    private String title;
    @ColumnInfo(name = "date_time")
    private String dateTime;
    @ColumnInfo(name = "subtitle")
    private String subtitle;
    @ColumnInfo(name= "note_text")
    private String notetText;
    @ColumnInfo(name="image_path")
    private String imagePath;
    @ColumnInfo(name = "color")
    private String color;
    @ColumnInfo(name = " web_link")
    private String link;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getNotetText() {
        return notetText;
    }

    public void setNotetText(String notetText) {
        this.notetText = notetText;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
    @NonNull
    @Override
    public String toString() {
        return title +":"+ dateTime;
    }
}

