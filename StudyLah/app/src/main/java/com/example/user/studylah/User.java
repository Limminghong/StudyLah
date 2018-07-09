package com.example.user.studylah;

public class User {
    private String username;
    private String imageLink;
    private String email;
    private String bio;

    User() {};

    User(String username, String imageLink, String email, String bio) {
        this.username = username;
        this.imageLink = imageLink;
        this.email = email;
        this.bio = bio;
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getUsername() {
        return username;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }
}
