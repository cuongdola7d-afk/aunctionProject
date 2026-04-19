package ddc.server.model.item;

public class Art extends Item {
    private String author;
    private String creationDate;

    public Art() {
        setCategory("ART");
    }

    public Art(String itemName, String description, double startingPrice) {
        super(itemName, description, startingPrice);
        setCategory("ART");
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}