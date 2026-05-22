
package ddc.server.dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ddc.server.config.DatabaseConnection;
import ddc.server.model.notification.Notification;

public class NotificationDAO {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationDAO.class);

    // Tạo notification mới
    public boolean create(Notification n) {
        String sql = "INSERT INTO ddc_notifications (user_id, type, auction_id, title, message) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, n.getUserId());
            pst.setString(2, n.getType());
            pst.setString(3, n.getAuctionId());
            pst.setString(4, n.getTitle());
            pst.setString(5, n.getMessage());
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Loi tao notification", e);
            return false;
        
}
    
}

    // Lấy danh sách notification theo userId, phân trang
    public List<Notification> getByUserId(String userId, int limit, int offset) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM ddc_notifications WHERE user_id = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";
        try (Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, userId);
            pst.setInt(2, limit);
            pst.setInt(3, offset);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                
}
            
}
        } catch (SQLException e) {
            LOGGER.error("Loi lay notification", e);
        
}
        return list;
    
}

    // Đếm số notification chưa đọc
    public int countUnread(String userId) {
        String sql = "SELECT COUNT(*) FROM ddc_notifications WHERE user_id = ? AND is_read = FALSE";
        try (Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next())
                    return rs.getInt(1);
            
}
        } catch (SQLException e) {
            LOGGER.error("Loi dem unread", e);
        
}
        return 0;
    
}

    // Đánh dấu đã đọc 1 notification
    public boolean markRead(String notificationId) {
        String sql = "UPDATE ddc_notifications SET is_read = TRUE WHERE id = ?";
        try (Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, notificationId);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.error("Loi mark read", e);
            return false;
        
}
    
}

    // Đánh dấu tất cả đã đọc cho 1 user
    public boolean markAllRead(String userId) {
        String sql = "UPDATE ddc_notifications SET is_read = TRUE WHERE user_id = ? AND is_read = FALSE";
        try (Connection con = getConnection();
                PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, userId);
            return pst.executeUpdate() >= 0;
        } catch (SQLException e) {
            LOGGER.error("Loi mark all read", e);
            return false;
        
}
    
}

    // Map ResultSet -> Notification
    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getString("id"));
        n.setUserId(rs.getString("user_id"));
        n.setType(rs.getString("type"));
        n.setAuctionId(rs.getString("auction_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setRead(rs.getBoolean("is_read"));
        n.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return n;
    
}


    protected Connection getConnection() throws SQLException {
        return ddc.server.config.DatabaseConnection.getConnection();
    }
}