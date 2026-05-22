package ddc.client.controller.profile;

import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.client.model.AuctionDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HistoryAuctionCard {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryBidCard.class);

    @FXML
    private Label idLabel, itemNameLabel, startTimeLabel, endTimeLabel, auctionStatusLabel;

    @FXML
    private Button cancelButton;

    private AuctionDTO auction;
    private String currentBidderId;

    public void setData(AuctionDTO auction, String currentBidderId) {
        this.auction = auction;
        this.currentBidderId = currentBidderId;

        idLabel.setText(auction.getId());
        itemNameLabel.setText(auction.getItem().getItemName());

        startTimeLabel.textProperty().unbind();
        startTimeLabel.textProperty().bind(Bindings.concat("◷ ", 
        new SimpleStringProperty(auction.getStartTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));
        
        endTimeLabel.textProperty().unbind();
        endTimeLabel.textProperty().bind(Bindings.concat("◷ ", 
        new SimpleStringProperty(auction.getEndTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));
        
        auctionStatusLabel.setText(auction.getStatus().name());
    }
}
