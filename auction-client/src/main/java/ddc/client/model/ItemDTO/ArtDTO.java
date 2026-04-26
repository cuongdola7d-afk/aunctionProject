package ddc.client.model.ItemDTO;

public class ArtDTO extends ItemGeneric<ArtDTO>{
    private String author;
    private int yearCreated;

    public ArtDTO () {}

    public static ArtDTO create() {
        return new ArtDTO();
    }

    public String getAuthor() { return author; }
    public int getyearCreated() { return yearCreated; }

    //Setters
    public ArtDTO setAuthor (String author) {
        this.author = author;
        return this;
    }

    public ArtDTO setyearCreated (int yearCreated) {
        this.yearCreated = yearCreated;
        return this;
    }
}