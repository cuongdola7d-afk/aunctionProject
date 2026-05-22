// package ddc.server.dao;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.ArgumentMatchers.anyString;
// import static org.mockito.Mockito.*;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.time.LocalDateTime;
// import java.util.List;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;

// import ddc.server.model.notification.Notification;

// public class NotificationDAOTest {

//     private NotificationDAO notificationDAO;

//     @Mock
//     private Connection mockConnection;
//     @Mock
//     private PreparedStatement mockPreparedStatement;
//     @Mock
//     private ResultSet mockResultSet;

//     @BeforeEach
//     public void setUp() throws SQLException {
//         MockitoAnnotations.openMocks(this);

//         notificationDAO = new NotificationDAO() {
//             @Override
//             protected Connection getConnection() throws SQLException {
//                 return mockConnection;
//             }
//         };

//         when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
//         when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
//     }

//     @Test
//     public void testCreate_Success() throws SQLException {
//         Notification n = new Notification();
//         n.setUserId("user1");
//         n.setType("TEST");
//         n.setAuctionId("auc1");
//         n.setTitle("Title");
//         n.setMessage("Msg");

//         when(mockPreparedStatement.executeUpdate()).thenReturn(1);
//         boolean result = notificationDAO.create(n);
//         assertTrue(result);
//         verify(mockPreparedStatement).setString(1, "user1");
//     }

//     @Test
//     public void testGetByUserId_Success() throws SQLException {
//         when(mockResultSet.next()).thenReturn(true, false);
//         when(mockResultSet.getString("id")).thenReturn("notif1");
//         when(mockResultSet.getString("user_id")).thenReturn("user1");
//         when(mockResultSet.getBoolean("is_read")).thenReturn(false);
//         when(mockResultSet.getObject("created_at", LocalDateTime.class)).thenReturn(LocalDateTime.now());

//         List<Notification> list = notificationDAO.getByUserId("user1", 10, 0);

//         assertNotNull(list);
//         assertEquals(1, list.size());
//         assertEquals("notif1", list.get(0).getId());
//     }

//     @Test
//     public void testCountUnread() throws SQLException {
//         when(mockResultSet.next()).thenReturn(true);
//         when(mockResultSet.getInt(1)).thenReturn(5);

//         int count = notificationDAO.countUnread("user1");
//         assertEquals(5, count);
//     }

//     @Test
//     public void testMarkRead() throws SQLException {
//         when(mockPreparedStatement.executeUpdate()).thenReturn(1);
//         boolean result = notificationDAO.markRead("notif1");
//         assertTrue(result);
//     }

//     @Test
//     public void testMarkAllRead() throws SQLException {
//         when(mockPreparedStatement.executeUpdate()).thenReturn(2);
//         boolean result = notificationDAO.markAllRead("user1");
//         assertTrue(result);
//     }
// }
