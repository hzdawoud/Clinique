package com.example.clinique.Models;

public class Patient {
    private String uid, name, id, dop, phone, address, history;

    public Patient(){

    }

    public Patient(String uid, String name, String id, String dop, String phone, String address, String history) {
        this.uid = uid;
        this.name = name;
        this.id = id;
        this.dop = dop;
        this.phone = phone;
        this.address = address;
        this.history = history;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDop() {
        return dop;
    }

    public void setDop(String dop) {
        this.dop = dop;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }
}
