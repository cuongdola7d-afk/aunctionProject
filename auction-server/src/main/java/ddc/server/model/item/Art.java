package ddc.server.model.item;

public class Art extends Item{
    private final String author;
    private final String creationDate;

    public Art (Builder builder) {
        super(builder);
        this.author = builder.author;
        this.creationDate = builder.creationDate;
    }

    public String getAuthor() { return author; }
    public String getCreationDate() { return creationDate; }

    public static class Builder extends ItemBuilder<Art, Builder> {
        private String author;
        private String creationDate;

        public Builder author (String author) {
            this.author = author;
            return this;
        }

        public Builder creationDate (String creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        @Override
        public Art build() {
            return new Art(this);
        }
    }
}