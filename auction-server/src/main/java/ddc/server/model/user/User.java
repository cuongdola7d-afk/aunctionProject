package ddc.server.model.user;

public class User {
    private final String action;
    private final String username;
    private final String name;
    private final String email;
    private final String password;

    protected User(Builder builder) {
        this.action = builder.action;
        this.username = builder.username;
        this.name = builder.name;
        this.email = builder.email;
        this.password = builder.password;
    }

    //Getter
    public String getAction() {return action;}
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

    public static class Builder {
        private String action;
        private String username;
        private String name;
        private String email;
        private String password;

        public Builder action (String action) {
            this.action = action;
            return this;
        }

        public Builder username (String username) {
            this.username = username;
            return this;
        }

        public Builder name (String name) {
            this.name = name;
            return this;
        }

        public Builder email (String email) {
            this.email = email;
            return this;
        }

        public Builder password (String password) {
            this.password = password;
            return this;
        }

        public User build () {
            return new User(this);
        }
    }
}
