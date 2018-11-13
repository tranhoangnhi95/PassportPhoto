package com.example.immortal.passportphoto.model;

public class MainFunction {
    private String name;
    private int image;

    public MainFunction() {
    }

    public MainFunction(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
