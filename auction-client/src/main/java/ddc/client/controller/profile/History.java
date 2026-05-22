package ddc.client.controller.profile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.controller.SceneSwitcher;
import ddc.client.model.AuctionDTO;
import ddc.client.model.BidDTO;
import ddc.client.model.Request;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.GetAllAuctionsResponse;
import ddc.client.network.response.GetAllUserBidResponse;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

public class History {
    @FXML
    private TreeView<String> bidTreeView;

    @FXML
    private FlowPane bidFlowPane;

    @FXML
    private ImageView loadingImageView;

    private static final Logger LOGGER = LoggerFactory.getLogger(History.class);
    private static final Gson gson = GsonConfig.newGson();
    private final Map<String, BidNodeHolder> bidCardCache = new HashMap<>();
    private final Map<String, AuctionNodeHolder> auctionCardCache = new HashMap<>();

    private final List<BidDTO> bidList = new ArrayList<>();
    private final List<AuctionDTO> auctionList = new ArrayList<>();

    private String selectedInTree;

    private String currentBidderId;

    @FXML
    public void initialize () {
        currentBidderId = UserSession.getInstance().getId();
        setUpTree();
        loadAllData();
    }

    private void loadAllData () {
        new Thread(() -> {
            try {
                String jsonBidResponse = RequestToServer.sendRequest(new Request().setAction("GET_ALL_USER_BIDS")
                                                                                   .setData(UserSession.getInstance().getUsername()));
                GetAllUserBidResponse bidResponse = gson.fromJson(jsonBidResponse, GetAllUserBidResponse.class);

                if ("SUCCESS".equals(bidResponse.getStatus())) {
                    List<BidDTO> bids = Arrays.asList(bidResponse.getData());

                    Platform.runLater(() -> {
                        bidList.clear();
                        bidList.addAll(bids);
                        if ("Lịch sử đấu giá".equals(selectedInTree)) {
                            renderBidHistory();
                        }
                    });
                }

                String jsonAuctionResponse = RequestToServer.sendRequest(new Request().setAction("GET_ALL_USER_AUCTIONS")
                                                                                       .setData(UserSession.getInstance().getUsername()));
                GetAllAuctionsResponse auctionResponse = gson.fromJson(jsonAuctionResponse, GetAllAuctionsResponse.class);

                if ("SUCCESS".equals(auctionResponse.getStatus())) {
                    List<AuctionDTO> auctions = Arrays.asList(auctionResponse.getData());

                    Platform.runLater(() -> {
                        auctionList.clear();
                        auctionList.addAll(auctions);
                        if ("Lịch sử đăng bán".equals(selectedInTree)) {
                            renderAuctionHistory();
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Lay du lieu that bai.");
                e.printStackTrace();
            }
        }).start();
    }

    private void setUpTree () {
        TreeItem<String> root = new TreeItem<>("Root");
        root.setExpanded(true);

        TreeItem<String> bid = new TreeItem<>("Lịch sử đấu giá");
        TreeItem<String> sell = new TreeItem<>("Lịch sử đăng bán");

        root.getChildren().addAll(bid, sell);

        bidTreeView.setRoot(root);
        bidTreeView.setShowRoot(false);

        bidTreeView.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (n == null) {
                selectedInTree = null;
                bidFlowPane.getChildren().clear();
            } else {
                selectedInTree = n.getValue();
                updateFlowPaneData();
            }
        });
    }

    private void updateFlowPaneData() {
        bidFlowPane.getChildren().clear();
        if ("Lịch sử đấu giá".equals(selectedInTree)) {
            renderBidHistory();
        } else if ("Lịch sử đăng bán".equals(selectedInTree)) {
            renderAuctionHistory();
        }
    }

    private void renderBidHistory () {
        if (bidList.isEmpty()) {
            Label nothing = new Label("KHÔNG CÓ LỊCH SỬ ĐẤU GIÁ.");
            nothing.setStyle("-fx-font: Tahoma; -fx-text-fill: #00008b; -fx-font-size: 40px");
            bidFlowPane.getChildren().add(nothing);
            return;
        }
        List<Parent> willBeRenderedNodes = new ArrayList<>();

        for (BidDTO bid : bidList) {
            try {
                BidNodeHolder holder = bidCardCache.get(bid.getId());

                if (holder == null) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/ddc/client/views/profile/HistoryBidCard.fxml"));
                    Parent cardNode = loader.load();
                    HistoryBidCard cardController = loader.getController();

                    holder = new BidNodeHolder(cardNode, cardController);
                    bidCardCache.put(bid.getId(), holder);
                }

                holder.controller.setData(bid, currentBidderId);
                willBeRenderedNodes.add(holder.cardNode);
            } catch (Exception e) {
                LOGGER.error("Không load được card", e);
                e.printStackTrace();
            }
        }
        bidFlowPane.getChildren().setAll(willBeRenderedNodes);
        bidCardCache.keySet().removeIf(id -> bidList.stream().noneMatch(i -> i.getId().equals(id)));
    }

    private void renderAuctionHistory () {
        if (auctionList.isEmpty()) {
            Label nothing = new Label("Không có lịch sử đăng bán");
            nothing.setStyle("-fx-font: Tahoma; -fx-text-fill: #00008b; -fx-font-size: 40px");
            bidFlowPane.getChildren().add(nothing);
            return;
        }
        List<Parent> willBeRenderedNodes = new ArrayList<>();

        for (AuctionDTO auction : auctionList) {
            try {
                AuctionNodeHolder holder = auctionCardCache.get(auction.getId());

                if (holder == null) {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/ddc/client/views/profile/HistoryAuctionCard.fxml"));
                    Parent cardNode = loader.load();
                    HistoryAuctionCard cardController = loader.getController();

                    holder = new AuctionNodeHolder(cardNode, cardController);
                    auctionCardCache.put(auction.getId(), holder);
                }

                holder.controller.setData(auction, currentBidderId);
                willBeRenderedNodes.add(holder.cardNode);
            } catch (Exception e) {
                LOGGER.error("Không load được card", e);
                e.printStackTrace();
            }
        }
        bidFlowPane.getChildren().setAll(willBeRenderedNodes);
        auctionCardCache.keySet().removeIf(id -> auctionList.stream().noneMatch(i -> i.getId().equals(id)));
    }

    private static class BidNodeHolder {
        final Parent cardNode;
        final HistoryBidCard controller;

        BidNodeHolder(Parent cardNode, HistoryBidCard controller) {
            this.cardNode = cardNode;
            this.controller = controller;
        }
    }

    private static class AuctionNodeHolder {
        final Parent cardNode;
        final HistoryAuctionCard controller;

        AuctionNodeHolder(Parent cardNode, HistoryAuctionCard controller) {
            this.cardNode = cardNode;
            this.controller = controller;
        }
    }

    @FXML
    private void switchBackToProfile(MouseEvent event) {
        SceneSwitcher.goTo(event, "/ddc/client/views/profile/Profile.fxml");
    }

}
