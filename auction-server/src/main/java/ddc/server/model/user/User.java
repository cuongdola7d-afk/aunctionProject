package ddc.server.model.user;

import ddc.server.model.entity.Entity;

public class User extends Entity<User> {
    private String username;
    private String name;
    private String email;
    private String password;
    private String role = "USER";
    private String status = "ACTIVE";

    public User() {}

    public String getUsername() { return username; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getStatus() { return status; }

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

    public User setRole(String role) {
        this.role = role == null || role.isBlank() ? "USER" : role;
        return this;
    }

    public User setStatus(String status) {
        this.status = status == null || status.isBlank() ? "ACTIVE" : status;
        return this;
    }
}
