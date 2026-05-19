package ddc.client.model;

public class UserDTO { 
    private String username;
    private String password;
    private String name;
    private String email;
    private String id;
    private String role;
    private String status;

    public UserDTO () {}

    //Getters
    public String getUsername () { return username; }
    public String getPassword () { return password; }
    public String getName () { return name; }
    public String getEmail () { return email; }
    public String getId () { return id;}
    public String getRole() { return role == null || role.isBlank() ? "USER" : role; }
    public String getStatus() { return status == null || status.isBlank() ? "ACTIVE" : status; }
    
    //Setters
    public UserDTO setUsername (String username) {
        this.username = username;
        return this;
    }

    public UserDTO setPassword (String password) {
        this.password = password;
        return this;
    }

    public UserDTO setName (String name) {
        this.name = name;
        return this;
    }

    public UserDTO setEmail (String email) {
        this.email = email;
        return this;
    }

    public UserDTO setId(String id){
        this.id = id;
        return this;
    }

    public UserDTO setRole(String role) {
        this.role = role == null || role.isBlank() ? "USER" : role;
        return this;
    }

    public UserDTO setStatus(String status) {
        this.status = status == null || status.isBlank() ? "ACTIVE" : status;
        return this;
    }
}
