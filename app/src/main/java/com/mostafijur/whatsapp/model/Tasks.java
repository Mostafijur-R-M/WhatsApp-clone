package com.mostafijur.whatsapp.model;

public class Tasks {
    private String date, taskId, taskName, taskStatus, text, time;

    public Tasks() {
    }

    public Tasks(String date, String taskId, String taskName, String taskStatus, String text, String time) {
        this.date = date;
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.text = text;
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
