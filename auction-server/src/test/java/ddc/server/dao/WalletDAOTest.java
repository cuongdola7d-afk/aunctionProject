package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletDAOTest {
    private static final String SELECT_BALANCE_SQL = "SELECT balance FROM ddc_wallets WHERE user_id = ?";
    private static final String UPDATE_BALANCE_SQL = "UPDATE ddc_wallets SET balance = balance + ? WHERE user_id = ?";
    private static final String INSERT_WALLET_SQL = "INSERT INTO ddc_wallets (user_id, balance) VALUES (?, ?)";
    private static final String INSERT_TRANSACTION_SQL =
            "INSERT INTO ddc_wallet_transactions (user_id, amount, transaction_type, description) VALUES (?, ?, ?, ?)";

    private Connection connection;
    private WalletDAO walletDAO;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        walletDAO = new WalletDAO() {
            @Override
            protected Connection getConnection() {
                return connection;
            }
        };
    }

    @Test
    void getBalance_shouldReturnZeroWhenWalletDoesNotExist() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(SELECT_BALANCE_SQL)).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        assertEquals(0, walletDAO.getBalance("missing-user"));
        verify(statement).setString(1, "missing-user");
    }

    @Test
    void getBalance_shouldReturnWalletBalanceWhenWalletExists() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement(SELECT_BALANCE_SQL)).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getDouble("balance")).thenReturn(125_000.0);

        assertEquals(125_000.0, walletDAO.getBalance("user1"));
    }

    @Test
    void updateBalance_shouldCreateWalletAndTransactionWhenWalletDoesNotExist() throws SQLException {
        PreparedStatement updateStatement = mock(PreparedStatement.class);
        PreparedStatement insertWalletStatement = mock(PreparedStatement.class);
        PreparedStatement transactionStatement = mock(PreparedStatement.class);

        when(connection.prepareStatement(eq(UPDATE_BALANCE_SQL))).thenReturn(updateStatement);
        when(connection.prepareStatement(eq(INSERT_WALLET_SQL))).thenReturn(insertWalletStatement);
        when(connection.prepareStatement(eq(INSERT_TRANSACTION_SQL))).thenReturn(transactionStatement);
        when(updateStatement.executeUpdate()).thenReturn(0);

        boolean success = walletDAO.updateBalance("user1", 100_000, "DEPOSIT", "Test deposit");

        assertTrue(success);
        verify(connection).setAutoCommit(false);
        verify(insertWalletStatement).setString(1, "user1");
        verify(insertWalletStatement).setDouble(2, 100_000);
        verify(transactionStatement).setString(3, "DEPOSIT");
        verify(connection).commit();
    }

    @Test
    void updateBalance_shouldIncrementExistingWalletAndCreateTransaction() throws SQLException {
        PreparedStatement updateStatement = mock(PreparedStatement.class);
        PreparedStatement transactionStatement = mock(PreparedStatement.class);

        when(connection.prepareStatement(eq(UPDATE_BALANCE_SQL))).thenReturn(updateStatement);
        when(connection.prepareStatement(eq(INSERT_TRANSACTION_SQL))).thenReturn(transactionStatement);
        when(updateStatement.executeUpdate()).thenReturn(1);

        boolean success = walletDAO.updateBalance("user1", -40_000, "DEDUCT_BID", "Test deduct");

        assertTrue(success);
        verify(updateStatement).setDouble(1, -40_000);
        verify(updateStatement).setString(2, "user1");
        verify(connection, never()).prepareStatement(INSERT_WALLET_SQL);
        verify(transactionStatement).setDouble(2, -40_000);
        verify(transactionStatement).setString(3, "DEDUCT_BID");
        verify(connection).commit();
    }

    @Test
    void updateBalance_shouldRollbackAndReturnFalseWhenSqlFails() throws SQLException {
        PreparedStatement updateStatement = mock(PreparedStatement.class);

        when(connection.prepareStatement(eq(UPDATE_BALANCE_SQL))).thenReturn(updateStatement);
        when(updateStatement.executeUpdate()).thenThrow(new SQLException("DB error"));

        boolean success = walletDAO.updateBalance("user1", 100_000, "DEPOSIT", "Test deposit");

        assertFalse(success);
        verify(connection).rollback();
    }
}
