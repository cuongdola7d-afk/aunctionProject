package ddc.server.model.user;

import ddc.server.model.entity.Entity;

public class User extends Entity<User> {
    private String username;
    private String name;
    private String email;
    private String password;
    
    private Bidder bidInfo;
    private Seller sellInfo;

    public User () {}

    //Getter
    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='[PROTECTED]'" +
                '}';
    }

    //Setter
    public User setUsername (String username) {
        this.username = username;
        return this;
    }

    public User setName (String name) {
        this.name = name;
        return this;
    }

    public User setEmail (String email) {
        this.email = email;
        return this;
    }

    public User setPassword (String password) {
        this.password = password;
        return this;
    }
}
