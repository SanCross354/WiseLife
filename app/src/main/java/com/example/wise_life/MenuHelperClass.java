package com.example.wise_life;

public class MenuHelperClass {

    int image;
    String name, calorie, carb, protein, fat;

    public MenuHelperClass(int image, String name, String calorie, String carb, String protein, String fat) {
        this.image = image;
        this.name = name;
        this.calorie = calorie;
        this.carb = carb;
        this.protein = protein;
        this.fat = fat;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalorie() {
        return calorie;
    }

    public void setCalorie(String calorie) {
        this.calorie = calorie;
    }

    public String getCarb() {
        return carb;
    }

    public void setCarb(String carb) {
        this.carb = carb;
    }

    public String getProtein() {
        return protein;
    }

    public void setProtein(String protein) {
        this.protein = protein;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }
}
