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
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class History {
    @FXML
    private Label historyLabel;

    @FXML
    private TreeView<String> bidTreeView;

    @FXML
    private FlowPane bidFlowPane;

    @FXML
    private ScrollPane bidScrollPane;

    @FXML
    private ImageView loadingImageView;

    @FXML
    private VBox loadingVBox;

    private static final Logger LOGGER = LoggerFactory.getLogger(History.class);
    private static final Gson gson = GsonConfig.newGson();
    private final Map<String, BidNodeHolder> bidCardCache = new HashMap<>();
    private final Map<String, AuctionNodeHolder> auctionCardCache = new HashMap<>();

    private final List<BidDTO> bidList = new ArrayList<>();
    private final List<AuctionDTO> auctionList = new ArrayList<>();

    private String selectedInTree;
    private String currentBidderId;

    private RotateTransition rotateTransition;
    private boolean isLoadingBids = true;
    private boolean isLoadingAuctions = true;

    @FXML
    public void initialize () {
        currentBidderId = UserSession.getInstance().getId();
        setupLoadingAnimation();
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
                        isLoadingBids = false;
                        if ("Lịch sử đấu giá".equals(selectedInTree)) {
                            updateFlowPaneData();
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
                        isLoadingAuctions = false;
                        if ("Lịch sử đăng bán".equals(selectedInTree)) {
                            updateFlowPaneData();
                        }
                    });
                }
            } catch (Exception e) {
                LOGGER.error("Lay du lieu that bai.");
                e.printStackTrace();
                Platform.runLater(() -> {
                    isLoadingBids = false;
                    isLoadingAuctions = false;
                    updateFlowPaneData(); 
                });
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
                hideLoading();
                bidFlowPane.getChildren().clear();
            } else {
                selectedInTree = n.getValue();
                updateFlowPaneData();
            }
        });

        bidTreeView.getSelectionModel().select(bid);
    }

    private void updateFlowPaneData() {
        bidFlowPane.getChildren().clear();
        if ("Lịch sử đấu giá".equals(selectedInTree)) {
            historyLabel.setText("LỊCH SỬ ĐẤU GIÁ");
            if (isLoadingBids) {
                showLoading();
            } else {
                hideLoading();
                renderBidHistory();
            }
        } else if ("Lịch sử đăng bán".equals(selectedInTree)) {
            historyLabel.setText("LỊCH SỬ ĐĂNG BÁN");
            if (isLoadingAuctions) {
                showLoading();
            } else {
                hideLoading();
                renderAuctionHistory();
            }
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

    private void setupLoadingAnimation() {
        rotateTransition = new RotateTransition(Duration.seconds(1), loadingImageView);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(Animation.INDEFINITE);
        rotateTransition.setInterpolator(Interpolator.LINEAR);
    }

    private void showLoading() {
        bidScrollPane.setVisible(false);
        loadingVBox.setVisible(true);
        rotateTransition.play();
    }

    private void hideLoading() {
        rotateTransition.stop();
        loadingVBox.setVisible(false);
        bidScrollPane.setVisible(true);
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
