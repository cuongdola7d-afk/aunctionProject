package ddc.client.model.ItemDTO;

public class ArtDTO extends ItemGeneric<ArtDTO>{
    private String author;
    private String yearCreated;

    public ArtDTO () {}

    public String getAuthor() { return author; }
    public String getyearCreated() { return yearCreated; }

    //Setters
    public ArtDTO setAuthor (String author) {
        this.author = author;
        return this;
    }

    public ArtDTO setyearCreated (String yearCreated) {
        this.yearCreated = yearCreated;
        return this;
    }
}