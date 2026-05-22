package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import ddc.server.config.DatabaseConnection;
import ddc.server.model.user.User;

public class UserDAOTest {

    private UserDAO userDAO;
    private MockedStatic<DatabaseConnection> mockedStaticDb;
    private Connection mockConnection;
    private PreparedStatement mockPreparedStatement;
    private ResultSet mockResultSet;
    private ResultSetMetaData mockMetaData;

    @BeforeEach
    public void setUp() throws SQLException {
        mockConnection = mock(Connection.class);
        mockPreparedStatement = mock(PreparedStatement.class);
        mockResultSet = mock(ResultSet.class);
        mockMetaData = mock(ResultSetMetaData.class);

        userDAO = new UserDAO() {
            @Override
            protected Connection getConnection() throws SQLException {
                return mockConnection;
            }
        };

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.getMetaData()).thenReturn(mockMetaData);
    }

    @AfterEach
    public void tearDown() {
    }

    @Test
    public void testRegisterUser_Success() throws SQLException {
        User user = new User().setUsername("testuser").setEmail("test@test.com").setPassword("pass");
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userDAO.registerUser(user);

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "testuser");
        verify(mockPreparedStatement).setString(3, "test@test.com");
    }

    @Test
    public void testRegisterUser_InvalidUser() {
        User user = new User().setUsername(""); // Invalid
        boolean result = userDAO.registerUser(user);
        assertFalse(result);
    }

    @Test
    public void testRegisterUser_SqlException() throws SQLException {
        User user = new User().setUsername("testuser").setEmail("test@test.com").setPassword("pass");
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Error"));

        boolean result = userDAO.registerUser(user);

        assertFalse(result);
    }

    @Test
    public void testGetUser_Success() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("id")).thenReturn("1");
        when(mockResultSet.getString("username")).thenReturn("testuser");
        when(mockMetaData.getColumnCount()).thenReturn(2);
        when(mockMetaData.getColumnName(1)).thenReturn("id");
        when(mockMetaData.getColumnName(2)).thenReturn("username");

        User result = userDAO.getUser("testuser");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    public void testGetUser_NotFound() throws SQLException {
        when(mockResultSet.next()).thenReturn(false);

        User result = userDAO.getUser("notfound");

        assertNull(result);
    }
    
    @Test
    public void testGetUserById_Success() throws SQLException {
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("id")).thenReturn("1");
        when(mockResultSet.getString("username")).thenReturn("testuser");
        when(mockMetaData.getColumnCount()).thenReturn(2);
        when(mockMetaData.getColumnName(1)).thenReturn("id");
        when(mockMetaData.getColumnName(2)).thenReturn("username");

        User result = userDAO.getUserById("1");

        assertNotNull(result);
        assertEquals("1", result.getId());
        assertEquals("testuser", result.getUsername());
    }

    @Test
    public void testChangePassword_Success() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userDAO.changePassword("testuser", "newpass");

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "newpass");
        verify(mockPreparedStatement).setString(2, "testuser");
    }

    @Test
    public void testChangePassword_Failure() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(0);

        boolean result = userDAO.changePassword("testuser", "newpass");

        assertFalse(result);
    }

    @Test
    public void testUpdateUserProfile_Success() throws SQLException {
        User user = new User().setId("1").setName("New Name").setEmail("new@email.com");
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = userDAO.updateUserProfile(user);

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "New Name");
        verify(mockPreparedStatement).setString(2, "new@email.com");
        verify(mockPreparedStatement).setString(3, "1");
    }
}
