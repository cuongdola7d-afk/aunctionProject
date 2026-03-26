package com.auction.entity.user;

public class Admin extends User {

    @Override
    public void printInfo() {
        System.out.println("Admin: " + name);
    }
}