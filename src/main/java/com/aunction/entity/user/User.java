package com.aunction.entity.user;

import com.aunction.entity.base.BaseEntity;

public abstract class User extends BaseEntity {
    protected String name;
    protected String email;
    protected String password;

    public abstract void printInfo();

    // getters/setters
}