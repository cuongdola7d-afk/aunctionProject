package ddc.client.network;

public class UserSession {
    private static UserSession instance;

    private String name;
    private String username;
    private String email;
    private String phone;
    private String id;

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

    public String getPhone() { return phone; }
    public UserSession setPhone(String phone) { 
        this.phone = phone; 
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

    public void cleanUserSession() {
        username = null;
        instance = null;
    }
}