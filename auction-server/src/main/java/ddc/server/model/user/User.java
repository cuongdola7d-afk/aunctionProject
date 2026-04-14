package ddc.server.model.user;

public class User {
    private final String action;
    private final String username;
    private final String name;
    private final String email;
    private final String password;

    protected User(UserBuilder<?, ?> builder) {
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

    public static abstract class UserBuilder<C extends User, B extends UserBuilder<C, B>> {
        private String action;
        private String username;
        private String name;
        private String email;
        private String password;

        public B action (String action) {
            this.action = action;
            return self();
        }

        public B username (String username) {
            this.username = username;
            return self();
        }

        public B name (String name) {
            this.name = name;
            return self();
        }

        public B email (String email) {
            this.email = email;
            return self();
        }

        public B password (String password) {
            this.password = password;
            return self();
        }

        protected B self() {
            return (B) this;
        }

        public abstract C build();
    }

    public static class Builder extends UserBuilder<User, Builder> {
        @Override
        public User build () {
            return new User(this);
        }
    }
}
