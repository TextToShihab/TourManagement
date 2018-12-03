package com.example.shihab.tourmanagement;

/**
 * Created by Kumu on 5/5/2017.
 */

public class Moments {

    private String title;
    private String description;
    private String image;

    public Moments(String title, String description, String image){

        this.title = title;
        this.description = description;
        this.image = image;
    }

    public Moments(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
