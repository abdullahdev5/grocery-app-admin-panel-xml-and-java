package com.grocery.groceryapp.models;

public class UserSearchesModel {

    String text, key;

    public UserSearchesModel() {
    }

    public UserSearchesModel(String text, String key) {
        this.text = text;
        this.key = key;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
