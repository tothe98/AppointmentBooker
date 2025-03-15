package com.example.appointmentbooker.Models;

public enum Role {
    User(1), Admin(2);

    private int role;

    public int getRole(){
        return this.role;
    }

    private Role(int role){
        this.role = role;
    }

}
