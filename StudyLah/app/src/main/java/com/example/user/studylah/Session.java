package com.example.user.studylah;

public class Session {
    String module;
    String timing;
    String location;

    public void setModule(String module) {
        this.module = module;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getModule() {
        return module;
    }

    public String getTiming() {
        return timing;
    }

    public String getLocation() {
        return location;
    }
}
