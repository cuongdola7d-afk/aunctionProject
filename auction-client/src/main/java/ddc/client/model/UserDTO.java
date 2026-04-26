package ddc.client.model;

public class UserDTO { 
    private String username;
    private String password;
    private String name;
    private String email;
    private String phone;
    private String id;

    public UserDTO () {}

    //Getters
    public String getUsername () { return username; }
    public String getPassword () { return password; }
    public String getName () { return name; }
    public String getEmail () { return email; }
    public String getPhone () { return phone;}
    public String getId () { return id;}
    
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

    public UserDTO setPhone(String phone){
        this.phone = phone;
        return this;
    }

    public UserDTO setId(String id){
        this.id = id;
        return this;
    }
}