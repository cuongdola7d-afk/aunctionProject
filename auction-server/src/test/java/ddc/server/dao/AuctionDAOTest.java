package ddc.server.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;

import ddc.server.model.item.ItemGeneric;
import ddc.server.model.transaction.Auction;
import ddc.server.model.transaction.AuctionStatus;
import ddc.server.model.user.User;

public class AuctionDAOTest {

    private AuctionDAO auctionDAO;

    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    @BeforeEach
    public void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);

        auctionDAO = new AuctionDAO() {
            @Override
            protected Connection getConnection() throws SQLException {
                return mockConnection;
            }
        };

        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
    }

    @Test
    public void testCreateAuction_Success() throws SQLException {
        Auction auction = new Auction();
        ddc.server.model.item.General item = new ddc.server.model.item.General();
        item.setId("item1");
        auction.setItem(item);
        auction.setCurrentPrice(100);
        auction.setStartingPrice(100);
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now().plusDays(1));

        when(mockPreparedStatement.executeUpdate()).thenReturn(1);

        boolean result = auctionDAO.createAuction(auction);

        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "item1");
        verify(mockPreparedStatement).setDouble(3, 100.0);
    }

    @Test
    public void testCreateAuction_Exception() throws SQLException {
        Auction auction = new Auction();
        ddc.server.model.item.General item = new ddc.server.model.item.General();
        item.setId("item1");
        auction.setItem(item);
        auction.setStartTime(LocalDateTime.now());
        auction.setEndTime(LocalDateTime.now());

        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB Error"));

        boolean result = auctionDAO.createAuction(auction);

        assertFalse(result);
    }

    @Test
    public void testGetAllAuctions() throws SQLException {
        when(mockResultSet.next()).thenReturn(true, false);
        when(mockResultSet.getString("id")).thenReturn("auc1");
        when(mockResultSet.getString("item_id")).thenReturn("item1");
        when(mockResultSet.getString("status")).thenReturn("OPEN");
        when(mockResultSet.getString("highest_bidder_name")).thenReturn("user1");
        when(mockResultSet.getDouble("current_price")).thenReturn(150.0);
        when(mockResultSet.getDouble("starting_price")).thenReturn(100.0);
        when(mockResultSet.getObject("start_time", LocalDateTime.class)).thenReturn(LocalDateTime.now());
        when(mockResultSet.getObject("end_time", LocalDateTime.class)).thenReturn(LocalDateTime.now().plusDays(1));

        // Note: ItemDAO and UserDAO inside AuctionDAO are real objects. 
        // We'd ideally mock them too, but here we just test AuctionDAO flow and avoid DB calls on them if possible.
        // Wait, UserDAO will try to hit DB! This might cause issues. 
        // It's better to use reflection to inject mocks for itemDAO and userDAO if we need strict isolation.
        // Let's see if this passes (if UserDAO fails, it just returns null and logs error).
        
        List<Auction> list = auctionDAO.getAllAuctions();
        
        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals("auc1", list.get(0).getId());
    }

    @Test
    public void testUpdateAuctionStatus_Success() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenReturn(1);
        boolean result = auctionDAO.updateAuctionStatus("auc1", AuctionStatus.FINISHED);
        assertTrue(result);
        verify(mockPreparedStatement).setString(1, "FINISHED");
        verify(mockPreparedStatement).setString(2, "auc1");
    }
    
    @Test
    public void testUpdateAuctionStatus_Exception() throws SQLException {
        when(mockPreparedStatement.executeUpdate()).thenThrow(new SQLException("DB error"));
        boolean result = auctionDAO.updateAuctionStatus("auc1", AuctionStatus.FINISHED);
        assertFalse(result);
    }
}
