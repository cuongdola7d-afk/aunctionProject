package ddc.client.network;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class UserSession {
    private static UserSession instance;

    private String name;
    private String username;
    private String email;
    private String id;
    private String password;
    private String role = "USER";
    private String status = "ACTIVE";
    private final IntegerProperty unreadCount = new SimpleIntegerProperty(0);

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUsername() { return username; }
    public UserSession setUsername(String username) { 
        this.username = username;
        return this; 
    }

    public String getEmail() { return email; }
    public UserSession setEmail(String email) { 
        this.email = email; 
        return this; 
    }


    public String getName() { return name; }
    public UserSession setName(String name) { 
        this.name = name; 
        return this; 
    }

    public String getId() { return id; }
    public UserSession setId(String id) { 
        this.id = id; 
        return this; 
    }

    public String getPassword(){ return password; }
    public UserSession setPassword(String password){
        this.password = password;
        return this;
    }

    public String getRole() { return role; }
    public UserSession setRole(String role) {
        this.role = role == null || role.isBlank() ? "USER" : role;
        return this;
    }

    public String getStatus() { return status; }
    public UserSession setStatus(String status) {
        this.status = status == null || status.isBlank() ? "ACTIVE" : status;
        return this;
    }

    public IntegerProperty unreadCountProperty() {
        return unreadCount;
    }

    public int getUnreadCount() {
        return unreadCount.get();
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount.set(unreadCount);
    }

    public void cleanUserSession() {
        username = null;
        name = null;
        email = null;
        id = null;
        password = null;
        role = "USER";
        status = "ACTIVE";
        instance = null;
    }
}
