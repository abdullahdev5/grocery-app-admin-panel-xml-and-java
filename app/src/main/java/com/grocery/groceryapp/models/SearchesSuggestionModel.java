package com.grocery.groceryapp.models;

public class SearchesSuggestionModel {

    String shortProductName;

    public SearchesSuggestionModel() {}

    public SearchesSuggestionModel(String shortProductName) {
        this.shortProductName = shortProductName;
    }

    public String getShortProductName() {
        return shortProductName;
    }

    public void setShortProductName(String shortProductName) {
        this.shortProductName = shortProductName;
    }
}
