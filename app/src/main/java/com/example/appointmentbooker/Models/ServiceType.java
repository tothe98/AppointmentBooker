package com.example.appointmentbooker.Models;

public class ServiceType {
    private int Id;
    private String name;
    private int period;

    public ServiceType() {
    }

    public ServiceType(int period, String name) {
        this.period = period;
        this.name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }
}
