package ddc.client.network.listener;

// Listener nhận event DASHBOARD_UPDATE và DASHBOARD_REFRESH từ GlobalSocketClient
public interface DashboardUpdateListener {
    void onDashboardUpdate(String auctionId, double currentPrice, String status, String endTime);

    // Yêu cầu reload toàn bộ danh sách (khi có auction mới hoặc status thay đổi lớn)
    default void onDashboardRefresh() {}
}
