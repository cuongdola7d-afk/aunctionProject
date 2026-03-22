package entity.user;

import entity.base.BaseEntity;

public abstract class User extends BaseEntity {
    protected String name;
    protected String email;
    protected String password;

    public abstract void printInfo();

    // getters/setters
}