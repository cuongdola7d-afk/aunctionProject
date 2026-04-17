package ddc.server.model.item;

public class Art extends ItemGeneric<Art>{
    private String author;
    private String creationDate;

    public Art () {}

    public String getAuthor() { return author; }
    public String getCreationDate() { return creationDate; }

    //Setters
    public Art setAuthor (String author) {
        this.author = author;
        return this;
    }

    public Art setCreationDate (String creationDate) {
        this.creationDate = creationDate;
        return this;
    }
}