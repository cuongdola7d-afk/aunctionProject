package ddc.client.model.ItemDTO;

public class ArtDTO extends ItemGeneric<ArtDTO>{
    private String author;
    private String creationDate;

    public ArtDTO () {}

    public String getAuthor() { return author; }
    public String getCreationDate() { return creationDate; }

    //Setters
    public ArtDTO setAuthor (String author) {
        this.author = author;
        return this;
    }

    public ArtDTO setCreationDate (String creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}