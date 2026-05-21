package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import ddc.server.config.DatabaseConnection;

class WalletDAOTest {
    private final WalletDAO walletDAO = new WalletDAO();

    @Test
    void getBalance_shouldReturnZeroWhenWalletDoesNotExist() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String userId = "missing-wallet-" + UUID.randomUUID();

        assertEquals(0, walletDAO.getBalance(userId));
    }

    @Test
    void updateBalance_shouldCreateWalletAndTransactionWhenWalletDoesNotExist() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String userId = null;
        try {
            userId = createTestUser();

            boolean success = walletDAO.updateBalance(userId, 100_000, "DEPOSIT", "Test deposit");

            assertTrue(success);
            assertEquals(100_000, walletDAO.getBalance(userId));
            assertEquals(1, countWalletTransactions(userId));
        } finally {
            deleteTestUserWalletData(userId);
        }
    }

    @Test
    void updateBalance_shouldIncrementExistingWalletAndCreateTransaction() {
        assumeTrue(hasDbConfig(), "Bo qua test DB khi chua cau hinh DDC_DB_*");

        String userId = null;
        try {
            userId = createTestUser();
            walletDAO.updateBalance(userId, 100_000, "DEPOSIT", "Initial deposit");

            boolean success = walletDAO.updateBalance(userId, -40_000, "DEDUCT_BID", "Test deduct");

            assertTrue(success);
            assertEquals(60_000, walletDAO.getBalance(userId));
            assertEquals(2, countWalletTransactions(userId));
        } finally {
            deleteTestUserWalletData(userId);
        }
    }

    private String createTestUser() {
        String token = UUID.randomUUID().toString().replace("-", "");
        String username = "wallet_test_" + token.substring(0, 12);
        String email = username + "@example.test";
        String sql = "INSERT INTO ddc_users (username, name, email, password) VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            pst.setString(2, "Wallet Test User");
            pst.setString(3, email);
            pst.setString(4, "password123");
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Khong the tao user test wallet.", e);
        }

        return findUserId(username);
    }

    private String findUserId(String username) {
        String sql = "SELECT id FROM ddc_users WHERE username = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the lay id user test wallet.", e);
        }
        throw new RuntimeException("Khong tim thay user test wallet: " + username);
    }

    private int countWalletTransactions(String userId) {
        String sql = "SELECT COUNT(*) FROM ddc_wallet_transactions WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the dem transaction wallet test.", e);
        }
        return 0;
    }

    private void deleteTestUserWalletData(String userId) {
        if (userId == null || userId.isBlank()) {
            return;
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                deleteByUserId(con, "ddc_wallet_transactions", userId);
                deleteByUserId(con, "ddc_wallets", userId);
                deleteById(con, "ddc_users", userId);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Khong the xoa du lieu test wallet userId=" + userId, e);
        }
    }

    private void deleteByUserId(Connection con, String tableName, String userId) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE user_id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, userId);
            pst.executeUpdate();
        }
    }

    private void deleteById(Connection con, String tableName, String id) throws SQLException {
        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, id);
            pst.executeUpdate();
        }
    }

    private boolean hasDbConfig() {
        return hasValue("DDC_DB_URL") && hasValue("DDC_DB_USER") && hasValue("DDC_DB_PASSWORD");
    }

    private boolean hasValue(String envName) {
        String value = System.getenv(envName);
        return value != null && !value.isBlank();
    }
}
