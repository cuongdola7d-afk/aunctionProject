package ddc.client.controller.profile;

import com.google.gson.Gson;
import ddc.client.config.ClientContext;
import ddc.client.config.GsonConfig;
import ddc.client.model.Request;
import ddc.client.network.RequestToServer;
import ddc.client.network.UserSession;
import ddc.client.network.request.DepositRequest;
import ddc.client.network.request.WalletRequest;
import ddc.client.network.response.DepositResponse;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Wallet {
    private static final Gson GSON = GsonConfig.newGson();

    @FXML
    private VBox walletView;

    @FXML
    private VBox depositView;

    @FXML
    private Label balanceLabel;

    @FXML
    private Label currentBalanceLabel;

    @FXML
    private TextField depositAmountField;

    @FXML
    private Button depositButton;

    @FXML
    private Button confirmDepositButton;

    private double balance;

    public void initialize() {
        showWalletView();
        loadBalance();
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleDeposit(ActionEvent event) {
        showDepositView();
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleBackToWallet(ActionEvent event) {
        showWalletView();
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleQuickAmount(ActionEvent event) {
        if (event.getSource() instanceof Button button) {
            depositAmountField.setText(button.getText().replace(".", ""));
        }
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleConfirmDeposit(ActionEvent event) {
        double amount = parseAmount(depositAmountField.getText());
        if (amount <= 0) {
            showAlert(Alert.AlertType.WARNING, "Vui lòng nhập số tiền hợp lệ.");
            return;
        }

        String userId = currentUserId();
        if (isBlank(userId)) {
            showMissingUserAlert();
            return;
        }

        setDepositFormDisabled(true);
        Task<DepositResponse> depositTask = new Task<>() {
            @Override
            protected DepositResponse call() {
                DepositRequest depositRequest = new DepositRequest(userId, amount);
                String responseJson = RequestToServer.sendRequest(new Request("DEPOSIT", depositRequest));
                return GSON.fromJson(responseJson, DepositResponse.class);
            }
        };

        depositTask.setOnSucceeded(eventSucceeded -> {
            setDepositFormDisabled(false);
            DepositResponse response = depositTask.getValue();
            if (isSuccess(response)) {
                balance = response.getBalance();
                refreshBalanceLabels();
                showAlert(Alert.AlertType.INFORMATION, "Nạp tiền thành công: " + formatBalance(amount));
                showWalletView();
                return;
            }

            showAlert(Alert.AlertType.WARNING, responseMessage(response, "Nạp tiền thất bại."));
        });

        depositTask.setOnFailed(eventFailed -> {
            setDepositFormDisabled(false);
            showAlert(Alert.AlertType.ERROR, "Lỗi khi nạp tiền.");
        });

        ClientContext.EXECUTOR.execute(depositTask);
    }

    @FXML
    @SuppressWarnings("unused")
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void loadBalance() {
        String userId = currentUserId();
        if (isBlank(userId)) {
            showMissingUserAlert();
            return;
        }

        balanceLabel.setText("Đang tải...");
        depositButton.setDisable(true);

        Task<DepositResponse> balanceTask = new Task<>() {
            @Override
            protected DepositResponse call() {
                WalletRequest walletRequest = new WalletRequest(userId);
                String responseJson = RequestToServer.sendRequest(new Request("GET_WALLET_BALANCE", walletRequest));
                return GSON.fromJson(responseJson, DepositResponse.class);
            }
        };

        balanceTask.setOnSucceeded(event -> {
            depositButton.setDisable(false);
            DepositResponse response = balanceTask.getValue();
            if (isSuccess(response)) {
                balance = response.getBalance();
                refreshBalanceLabels();
                return;
            }

            refreshBalanceLabels();
            showAlert(Alert.AlertType.WARNING, responseMessage(response, "Không lấy được số dư ví."));
        });

        balanceTask.setOnFailed(event -> {
            depositButton.setDisable(false);
            refreshBalanceLabels();
            showAlert(Alert.AlertType.ERROR, "Lỗi khi lấy số dư ví.");
        });

        ClientContext.EXECUTOR.execute(balanceTask);
    }

    private void showWalletView() {
        setViewVisible(walletView, true);
        setViewVisible(depositView, false);
        depositAmountField.clear();
        refreshBalanceLabels();
    }

    private void showDepositView() {
        setViewVisible(walletView, false);
        setViewVisible(depositView, true);
        refreshBalanceLabels();
        depositAmountField.requestFocus();
    }

    private void refreshBalanceLabels() {
        String formattedBalance = formatBalance(balance);
        balanceLabel.setText(formattedBalance);
        currentBalanceLabel.setText("Số dư hiện tại: " + formattedBalance);
    }

    private void setViewVisible(VBox view, boolean visible) {
        view.setVisible(visible);
        view.setManaged(visible);
    }

    private void setDepositFormDisabled(boolean disabled) {
        depositAmountField.setDisable(disabled);
        confirmDepositButton.setDisable(disabled);
    }

    private String currentUserId() {
        return UserSession.getInstance().getId();
    }

    private boolean isSuccess(DepositResponse response) {
        return response != null && "SUCCESS".equals(response.getStatus());
    }

    private String responseMessage(DepositResponse response, String defaultMessage) {
        return response == null || isBlank(response.getMessage()) ? defaultMessage : response.getMessage();
    }

    private void showMissingUserAlert() {
        showAlert(Alert.AlertType.WARNING, "Không tìm thấy thông tin người dùng. Vui lòng đăng nhập lại.");
    }

    private double parseAmount(String rawAmount) {
        if (rawAmount == null) {
            return 0;
        }

        try {
            String normalizedAmount = rawAmount.trim().replace(".", "").replace(",", "");
            return Double.parseDouble(normalizedAmount);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Ví của tôi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String formatBalance(double balance) {
        return String.format("%,.0f VND", balance);
    }
}
