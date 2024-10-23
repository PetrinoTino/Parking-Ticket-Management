package com.sda;


public class Customer {
    private int id;
    private String firstName;
    private String lastName;
    private boolean isMember;

    public Customer(int id, String firstName, String lastName, boolean isMember) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isMember = isMember;
    }

    public int getId() {
        return id;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isMember() {
        return isMember;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

