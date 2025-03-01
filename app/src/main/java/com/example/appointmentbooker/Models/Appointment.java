package com.example.appointmentbooker.Models;

import java.util.Date;

public class Appointment {
    private int Id;
    private String datetime;
    private int period; //minutes
    private String bookedEmail;
    private String serviceType;

    public Appointment() {
    }

    public Appointment(String datetime, int period) {
        this.datetime = datetime;
        this.period = period;
        this.bookedEmail = null;
        this.serviceType = null;
    }

    public Appointment(int id, String datetime, int period) {
        Id = id;
        this.datetime = datetime;
        this.period = period;
        this.bookedEmail = null;
        this.serviceType = null;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getBookedEmail() {
        return bookedEmail;
    }

    public void setBookedEmail(String booked_email) {
        this.bookedEmail = booked_email;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String service_type) {
        this.serviceType = service_type;
    }
}
