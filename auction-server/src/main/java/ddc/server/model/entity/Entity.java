package ddc.server.model.entity;

public abstract class Entity <T extends Entity<T>> {
    private String id;

    public Entity () {}

    public String getId () { return id; }

    protected T self () {
        return (T) this;
    }

    public T setId (String id) {
        this.id = id;
        return self();
    }
}