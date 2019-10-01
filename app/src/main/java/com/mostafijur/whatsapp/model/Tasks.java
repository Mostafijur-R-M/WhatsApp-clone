package com.mostafijur.whatsapp.model;

public class Tasks {
    private String taskName, taskID, status;

    public Tasks(String taskName, String taskID, String status) {
        this.taskName = taskName;
        this.taskID = taskID;
        this.status = status;
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
}
