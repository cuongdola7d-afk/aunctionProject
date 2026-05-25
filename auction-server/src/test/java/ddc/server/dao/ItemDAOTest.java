package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ddc.server.model.item.ItemGeneric;

public class ItemDAOTest {
    private Connection connection;
    private ItemDAO itemDAO;

    @BeforeEach
    void setUp() {
        connection = mock(Connection.class);
        itemDAO = new ItemDAO() {
            @Override
            protected Connection getConnection() {
                return connection;
            }
        };
    }

    @Test
    void addItem_shouldCommitAndReturnGeneratedId() throws SQLException {
        ItemGeneric<?> item = mock(ItemGeneric.class);
        when(item.save(connection)).thenReturn("I_TEST");

        String generatedId = itemDAO.addItem(item);

        assertEquals("I_TEST", generatedId);
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
    }

    @Test
    void addItem_shouldRollbackAndReturnNullWhenSaveFails() throws SQLException {
        ItemGeneric<?> item = mock(ItemGeneric.class);
        when(item.save(connection)).thenThrow(new SQLException("DB error"));

        String generatedId = itemDAO.addItem(item);

        assertNull(generatedId);
        verify(connection).rollback();
    }

    @Test
    void getItem_shouldMapGeneralItemFromResultSet() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement("SELECT * FROM ddc_items WHERE id = ?")).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getString("id")).thenReturn("I00005");
        when(resultSet.getString("item_name")).thenReturn("Tranh");
        when(resultSet.getString("description")).thenReturn("abc");
        when(resultSet.getString("category")).thenReturn("GENERAL");
        when(resultSet.getString("seller_name")).thenReturn("cuongdo123");
        when(resultSet.getString("image_url")).thenReturn("image.png");

        ItemGeneric<?> retrieved = itemDAO.getItem("I00005");

        assertNotNull(retrieved);
        assertEquals("I00005", retrieved.getId());
        assertEquals("Tranh", retrieved.getItemName());
        verify(statement).setString(1, "I00005");
    }

    @Test
    void getItem_shouldReturnNullWhenItemDoesNotExist() throws SQLException {
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        when(connection.prepareStatement("SELECT * FROM ddc_items WHERE id = ?")).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        ItemGeneric<?> retrieved = itemDAO.getItem("missing");

        assertNull(retrieved);
        verify(resultSet).next();
    }
}
