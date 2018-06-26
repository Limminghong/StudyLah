package com.example.user.studylah;

import java.util.ArrayList;

public class Session {
    String host;
    String module;
    String timing;
    String date;
    String location;
    String[] participants;

    public void setHost(String host) {this.host = host;}

    public void setModule(String module) {
        this.module = module;
    }

    public void setTiming(String timing) {
        this.timing = timing;
    }

    public void setDate(String date) {this.date = date;}

    public void setLocation(String location) {
        this.location = location;
    }

    public void setParticipants(String[] participants) {this.participants = participants; }

    public String getHost() {
        return host;
    }

    public String getModule() {
        return module;
    }

    public String getTiming() {
        return timing;
    }

    public String getdate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public String[] getParticipants() {return participants; }
}
