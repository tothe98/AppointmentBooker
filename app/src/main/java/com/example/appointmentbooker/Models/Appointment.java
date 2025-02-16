package com.example.appointmentbooker.Models;

import java.util.Date;

public class Appointment {
    private String startDatetime;
    private int period; //minutes
    private String endDatetime;
    private String title;
    private String bookedEmail;
    private String serviceName;

    public Appointment() {
    }

    public Appointment(String startDatetime, int period, String endPeriod, String title) {
        this.startDatetime = startDatetime;
        this.period = period;
        this.endDatetime = endPeriod;
        this.title = title;
        this.bookedEmail = null;
        this.serviceName = null;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getBookedEmail() {
        return bookedEmail;
    }

    public void setBookedEmail(String bookedEmail) {
        this.bookedEmail = bookedEmail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(String endDatetime) {
        this.endDatetime = endDatetime;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(String startDatetime) {
        this.startDatetime = startDatetime;
    }
}
