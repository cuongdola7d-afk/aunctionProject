
package entity.item;
 class Art extends Item {
    private String artist;
    private int year;

    @Override
    public String getCategory() {
        return "Art";
    }
}