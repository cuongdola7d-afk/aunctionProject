package ddc.client.controller.profile;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ddc.client.model.BidDTO;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HistoryBidCard {
    private static final Logger LOGGER = LoggerFactory.getLogger(HistoryBidCard.class);

    @FXML
    private Label idLabel, itemNameLabel, bidPriceLabel, bidTimeLabel, auctionStatusLabel;

    private BidDTO bid;
    private String currentBidderId;

    public void setData(BidDTO bid, String currentBidderId) {
        this.bid = bid;
        this.currentBidderId = currentBidderId;

        idLabel.setText(bid.getId());
        itemNameLabel.setText(bid.getAuction().getItem().getItemName());
        bidPriceLabel.setText(new DecimalFormat("#,###").format(bid.getBidAmount()) + " đ");
        bidTimeLabel.textProperty().unbind();
        bidTimeLabel.textProperty().bind(Bindings.concat("◷ ", 
        new SimpleStringProperty(bid.getBidTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))));
        auctionStatusLabel.setText(bid.getAuction().getStatus().name());
    }
}
