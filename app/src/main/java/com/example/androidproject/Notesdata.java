package com.example.androidproject;

import java.util.ArrayList;

public class Notesdata {

    int id;
    String category , notesTitle, description, date;

    public Notesdata(int id) {
        this.id = id;
        this.category = category;
        this.notesTitle = notesTitle;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public String getNotesTitle() {
        return notesTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public static ArrayList<Notesdata> notesdata = new ArrayList<>();

}
