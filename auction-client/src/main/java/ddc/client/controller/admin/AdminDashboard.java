package ddc.client.controller.admin;

import java.util.Map;

import com.google.gson.Gson;

import ddc.client.config.GsonConfig;
import ddc.client.model.AuctionDTO;
import ddc.client.model.Request;
import ddc.client.model.UserDTO;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.response.AdminStatsResponse;
import ddc.client.network.response.BaseResponse;
import ddc.client.network.response.GetAllAuctionsResponse;
import ddc.client.network.response.UserListResponse;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AdminDashboard {
    @FXML private Label adminNameLabel;
    @FXML private Label statusLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalItemsLabel;
    @FXML private Label totalAuctionsLabel;
    @FXML private Label runningAuctionsLabel;

    @FXML private TableView<UserDTO> userTable;
    @FXML private TableColumn<UserDTO, String> userIdColumn;
    @FXML private TableColumn<UserDTO, String> usernameColumn;
    @FXML private TableColumn<UserDTO, String> nameColumn;
    @FXML private TableColumn<UserDTO, String> emailColumn;
    @FXML private TableColumn<UserDTO, String> roleColumn;
    @FXML private TableColumn<UserDTO, String> userStatusColumn;

    @FXML private TableView<AuctionDTO> auctionTable;
    @FXML private TableColumn<AuctionDTO, String> auctionIdColumn;
    @FXML private TableColumn<AuctionDTO, String> itemColumn;
    @FXML private TableColumn<AuctionDTO, String> sellerColumn;
    @FXML private TableColumn<AuctionDTO, String> priceColumn;
    @FXML private TableColumn<AuctionDTO, String> auctionStatusColumn;

    private final Gson gson = GsonConfig.newGson();

    @FXML
    private void initialize() {
        UserSession session = UserSession.getInstance();
        adminNameLabel.setText(session.getName() == null ? session.getUsername() : session.getName());

        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        userStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        auctionIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuctionId()));
        itemColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getItem() == null ? "" : data.getValue().getItem().getItemName()));
        sellerColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getItem() == null ? "" : data.getValue().getItem().getSellerName()));
        priceColumn.setCellValueFactory(data -> new SimpleStringProperty(String.format("%,.0f", data.getValue().getCurrentPrice())));
        auctionStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatus() == null ? "" : data.getValue().getStatus().name()));

        refreshAll();
    }

    @FXML
    private void refreshAll() {
        loadStats();
        loadUsers();
        loadAuctions();
    }

    @FXML
    private void blockSelectedUser() {
        updateSelectedUserStatus("BLOCKED");
    }

    @FXML
    private void activateSelectedUser() {
        updateSelectedUserStatus("ACTIVE");
    }

    @FXML
    private void deleteSelectedUser() {
        UserDTO selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Select a user first.");
            return;
        }

        BaseResponse response = sendBase("ADMIN_DELETE_USER", new AdminUserRequest(selected.getId(), null));
        showInfo(response.getMessage());
        loadUsers();
        loadStats();
    }

    @FXML
    private void cancelSelectedAuction() {
        AuctionDTO selected = auctionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Select an auction first.");
            return;
        }

        BaseResponse response = sendBase("ADMIN_CANCEL_AUCTION", new AdminAuctionRequest(selected.getAuctionId()));
        showInfo(response.getMessage());
        loadAuctions();
        loadStats();
    }

    @FXML
    private void logout() {
        UserSession.getInstance().cleanUserSession();
        Stage stage = (Stage) adminNameLabel.getScene().getWindow();
        stage.close();
    }

    private void loadStats() {
        String responseJson = RequestToServer.sendRequest(
                new Request().setAction("ADMIN_STATS").setData(new AdminOnlyRequest()));
        AdminStatsResponse response = gson.fromJson(responseJson, AdminStatsResponse.class);

        if (response == null || !"SUCCESS".equals(response.getStatus()) || response.getData() == null) {
            statusLabel.setText("Cannot load admin stats.");
            return;
        }

        Map<String, Integer> stats = response.getData();
        totalUsersLabel.setText(String.valueOf(stats.getOrDefault("users", 0)));
        totalItemsLabel.setText(String.valueOf(stats.getOrDefault("items", 0)));
        totalAuctionsLabel.setText(String.valueOf(stats.getOrDefault("auctions", 0)));
        runningAuctionsLabel.setText(String.valueOf(stats.getOrDefault("runningAuctions", 0)));
        statusLabel.setText("Ready");
    }

    private void loadUsers() {
        String responseJson = RequestToServer.sendRequest(
                new Request().setAction("ADMIN_GET_USERS").setData(new AdminOnlyRequest()));
        UserListResponse response = gson.fromJson(responseJson, UserListResponse.class);

        if (response != null && "SUCCESS".equals(response.getStatus()) && response.getData() != null) {
            userTable.setItems(FXCollections.observableArrayList(response.getData()));
        } else {
            statusLabel.setText("Cannot load users.");
        }
    }

    private void loadAuctions() {
        String responseJson = RequestToServer.sendRequest(new Request().setAction("GET_ALL").setData(null));
        GetAllAuctionsResponse response = gson.fromJson(responseJson, GetAllAuctionsResponse.class);

        if (response != null && "SUCCESS".equals(response.getStatus()) && response.getData() != null) {
            auctionTable.setItems(FXCollections.observableArrayList(response.getData()));
        } else {
            statusLabel.setText("Cannot load auctions.");
        }
    }

    private void updateSelectedUserStatus(String status) {
        UserDTO selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showInfo("Select a user first.");
            return;
        }

        BaseResponse response = sendBase("ADMIN_UPDATE_USER_STATUS", new AdminUserRequest(selected.getId(), status));
        showInfo(response.getMessage());
        loadUsers();
    }

    private BaseResponse sendBase(String action, Object data) {
        String responseJson = RequestToServer.sendRequest(new Request().setAction(action).setData(data));
        BaseResponse response = gson.fromJson(responseJson, BaseResponse.class);
        return response == null ? new BaseResponse().setStatus("FAILED").setMessage("No response from server.") : response;
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(message == null || message.isBlank() ? "Done." : message);
        alert.showAndWait();
    }

    private static class AdminOnlyRequest {
        final String adminUsername = UserSession.getInstance().getUsername();
    }

    private static class AdminUserRequest extends AdminOnlyRequest {
        final String userId;
        final String status;

        AdminUserRequest(String userId, String status) {
            this.userId = userId;
            this.status = status;
        }
    }

    private static class AdminAuctionRequest extends AdminOnlyRequest {
        final String auctionId;

        AdminAuctionRequest(String auctionId) {
            this.auctionId = auctionId;
        }
    }
}
