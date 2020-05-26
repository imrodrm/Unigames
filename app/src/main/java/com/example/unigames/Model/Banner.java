package com.example.unigames.Model;

public class Banner { //No se ha usado
    private String name,image;

    public Banner() {

    }

    public Banner(String name, String image) {
        this.name = name;
        this.image = image;
    }



    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
