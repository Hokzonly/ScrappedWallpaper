package com.Africa.Wallpapers4Knewtest.models;

public class CategoryListModel {
    String name,description,image,url;


    public CategoryListModel(String name, String description, String image, String url) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public String getUrl() {
        return url;
    }
}
