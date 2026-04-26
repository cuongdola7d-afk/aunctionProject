package ddc.client.network;

public class UserSession {
    private static UserSession instance;

    private String name;
    private String username;
    private String email;
    private String phone;
    private String Id;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getId() { return Id; }
    public void setId(String name) { this.Id = Id; }

    public void cleanUserSession() {
        username = null;
        instance = null;
    }
}