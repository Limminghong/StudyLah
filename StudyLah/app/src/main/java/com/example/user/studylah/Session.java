package com.example.user.studylah;

import java.util.HashMap;
import java.util.Map;

public class Session {
    String id;
    String host;
    String module;
    String timing;
    String date;
    String location;

    int participantCount = 0;
    Map<String, Boolean> participants = new HashMap<>();

    Session(){};

    public void setId(String id) {
        this.id = id;
    }

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

    public void setParticipantCount(int count) {
        this.participantCount = count;
    }

    public void setParticipants(Map<String, Boolean> participants) {
        this.participants = participants;
    }

    public String getId() {return id;}

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

    public int getParticipantCount() {
        return participantCount;
    }

    public Map<String, Boolean> getParticipants() { return participants; }
}
