package com.example.unigames;

public class IndividualCreators {
    private String name;
    private String slug;
    private String image;

    public IndividualCreators(String n, String s, String i){
        this.name=n;
        this.slug=s;
        this.image=i;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getSlug() {
        return slug;
    }
}
