package com.grocery.groceryapp.models;

public class CategoryModel {

    private String name, icon;
    private int id, color;

    public CategoryModel() {
    }


    public CategoryModel(String name, String icon, int color, int id) {
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
