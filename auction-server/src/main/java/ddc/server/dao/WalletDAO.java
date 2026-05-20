package ddc.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ddc.server.config.DatabaseConnection;


public class WalletDAO {
    
    public double getBalance(String userId) {
        String sql = "SELECT balance FROM ddc_wallets WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) { e.printStackTrace(); }
        return 0.0;
    }

    public boolean updateBalance(String userId, double amount, String type, String description) {
        String updateSql = "UPDATE ddc_wallets SET balance = balance + ? WHERE user_id = ?";
        String logSql = "INSERT INTO ddc_wallet_transactions (user_id, amount, transaction_type, description) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmtUpdate = conn.prepareStatement(updateSql)) {
                stmtUpdate.setDouble(1, amount);
                stmtUpdate.setString(2, userId);
                int affectedRows = stmtUpdate.executeUpdate();
                if (affectedRows == 0) {
                    String insertWallet = "INSERT INTO ddc_wallets (user_id, balance) VALUES (?, ?)";
                    try (PreparedStatement stmtIns = conn.prepareStatement(insertWallet)) {
                        stmtIns.setString(1, userId);
                        stmtIns.setDouble(2, amount);
                        stmtIns.executeUpdate();
                    }
                }
            }

            try (PreparedStatement stmtLog = conn.prepareStatement(logSql)) {
                stmtLog.setString(1, userId);
                stmtLog.setDouble(2, amount);
                stmtLog.setString(3, type);
                stmtLog.setString(4, description);
                stmtLog.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) { try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) { try { conn.close(); } catch (SQLException e) { e.printStackTrace(); } }
        }
    }
}