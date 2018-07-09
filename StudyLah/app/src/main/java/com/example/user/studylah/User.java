package com.example.user.studylah;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String imageLink;
    private String imageThumb;
    private String email;
    private String bio;
    Map<String, Boolean> hostedSessions = new HashMap<>();

    User() {};

    User(String username, String imageLink, String imageThumb, String email, String bio) {
        this.username = username;
        this.imageLink = imageLink;
        this.imageThumb = imageThumb;
        this.email = email;
        this.bio = bio;
    };

    public void setUsername(String username) {
        this.username = username;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setImageThumbLink(String imageThumb) {
        this.imageThumb = imageThumb;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setHostedSessions(Map<String, Boolean> hostedSessions) {
        this.hostedSessions = hostedSessions;
    }

    public String getUsername() {
        return username;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public String getEmail() {
        return email;
    }

    public String getBio() {
        return bio;
    }

    public Map<String, Boolean> getHostedSessions() {
        return hostedSessions;
    }
}
