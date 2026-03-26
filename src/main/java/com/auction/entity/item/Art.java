package com.auction.entity.item;

public class Art extends Item {
    private String artist;
    private int year;

    public Art(String artist, int year){
         this.artist = artist;
         this.year = year;
    }

    @Override
    public String getCategory() {
        return "Art";
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}