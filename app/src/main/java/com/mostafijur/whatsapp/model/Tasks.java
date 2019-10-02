package com.mostafijur.whatsapp.model;

public class Tasks {
    private String taskName, taskID, status, type, time, date, from;

    public Tasks() {
    }

    public Tasks(String taskName, String taskID, String status, String type, String time, String date, String from) {
        this.taskName = taskName;
        this.taskID = taskID;
        this.status = status;
        this.type = type;
        this.time = time;
        this.date = date;
        this.from = from;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskID() {
        return taskID;
    }

    public void setTaskID(String taskID) {
        this.taskID = taskID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }
}
