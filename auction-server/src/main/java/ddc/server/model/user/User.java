package ddc.server.model.user;
import ddc.server.model.entity.*;
public abstract class User extends BaseEntity {
    protected String name;
    protected String email;
    protected String password;

    public abstract void printInfo();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}