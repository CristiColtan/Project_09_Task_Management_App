package com.example.cctask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task {
    private String title;
    private String assignedTo;
    private String createdBy; // username-ul userului
    private Date dataExpirare;
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public Task(String title, String assignedTo, String createdBy, Date dataExpirare) {
        this.title = title;
        this.assignedTo = assignedTo;
        this.createdBy = createdBy;
        this.dataExpirare = dataExpirare;
    }

    public String getTitle() {
        return title;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Date getDataExpirare() {
        return dataExpirare;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setDataExpirare(Date dataExpirare) {
        this.dataExpirare = dataExpirare;
    }
}
