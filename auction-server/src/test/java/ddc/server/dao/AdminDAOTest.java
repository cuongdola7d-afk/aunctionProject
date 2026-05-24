package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AdminDAOTest {

    private AdminDAO adminDAO;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @Mock
    private Statement mockStatement;
    @Mock
    private java.sql.ResultSetMetaData mockMetaData;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        adminDAO = new AdminDAO() {
            @Override
            protected Connection getConnection() throws SQLException {
                return mockConnection;
            }
        };

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockConnection.createStatement()).thenReturn(mockStatement);
        when(mockStatement.executeQuery(anyString())).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
        when(mockMetaData.getColumnCount()).thenReturn(1);
        when(mockMetaData.getColumnName(1)).thenReturn("status");
    }

    @Test
    public void testDeleteUser_Success() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = adminDAO.deleteUser("user1");
        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "user1");
    }

    @Test
    public void testDeleteUser_Failure() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);
        boolean result = adminDAO.deleteUser("user1");
        assertFalse(result);
    }

    @Test
    public void testDeleteUser_Exception() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB error"));
        boolean result = adminDAO.deleteUser("user1");
        assertFalse(result);
    }

    @Test
    public void testUpdateUserStatus_Success() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = adminDAO.updateUserStatus("user1", "BLOCKED");
        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "BLOCKED");
        verify(mockPreparedStatement).setString(2, "user1");
    }

    @Test
    public void testUpdateUserStatus_RejectsDeleted() throws SQLException {
        boolean result = adminDAO.updateUserStatus("user1", "DELETED");
        assertFalse(result);
        verify(mockConnection, never()).prepareStatement("UPDATE ddc_users SET status = ? WHERE id = ?");
    }
}
