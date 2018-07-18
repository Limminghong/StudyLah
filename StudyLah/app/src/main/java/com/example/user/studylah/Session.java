package com.example.user.studylah;

import java.util.HashMap;
import java.util.Map;

public class Session {
    private String id;
    private String host;
    private String module;
    private String timingFrom;
    private String timingTo;
    private String date;
    private String location;
    private String sessionInformation;
    private String hostImage;

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

    public void setTimingFrom(String timingFrom) {
        this.timingFrom = timingFrom;
    }

    public void setTimingTo(String timingTo) {
        this.timingTo = timingTo;
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

    public void setSessionInformation(String info) {
        this.sessionInformation = info;
    }

    public void setHostImage(String link) {
        this.hostImage = link;
    }

    public String getId() {return id;}

    public String getHost() {
        return host;
    }

    public String getModule() {
        return module;
    }

    public String getTimingFrom() {
        return timingFrom;
    }

    public String getTimingTo() { return timingTo; }

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

    public String getSessionInformation() { return sessionInformation; }

    public String getHostImage() {
        return hostImage;
    }
}
