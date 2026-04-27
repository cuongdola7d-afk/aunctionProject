// package ddc.server.dao;

// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.sql.SQLException;
// import java.util.ArrayList;
// import java.util.List;

// import ddc.server.config.DatabaseConnection;
// import ddc.server.exception.ItemValidationException;
// import ddc.server.model.item.Art;
// import ddc.server.model.item.Electronics;
// import ddc.server.model.item.ItemGeneric;
// import ddc.server.model.item.Vehicle;
// import ddc.server.pattern.factory.ItemCreating.CreatorRegistry;
// import ddc.server.pattern.factory.ItemCreating.ItemRequest;

// public class ItemDAO {

//    public boolean addItem (ItemGeneric item) {
//         try (Connection con = DatabaseConnection.getConnection()) {
//             con.setAutoCommit(false);

//             try {
//                 item.save(con);

//                 con.commit();
//                 return true;
//             } catch (SQLException e) {
//                 con.rollback();
//                 e.printStackTrace();
//                 return false;
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//             return false;
//         }
        
//     }

//     public ItemGeneric getItem(String id) {
//         String sql = "SELECT * FROM ddc_items WHERE id = ?";

//         try (Connection con = DatabaseConnection.getConnection();
//             PreparedStatement pst = con.prepareStatement(sql)) {

//             pst.setString(1, id);
//             try (ResultSet rs = pst.executeQuery()) {
//                 if (rs.next()) {
//                     // Bước 1: Đổ dữ liệu từ DB vào ItemRequest (DTO)
//                     ItemRequest request = new ItemRequest(rs.getString("item_name"),
//                                                           rs.getString("description"),
//                                                           rs.getString("category"),
//                                                           rs.getString("seller_name"));
                       
//                     // Gán ID,cate để Factory hoặc các bước sau sử dụng
//                     String category = rs.getString("category");
//                     String itemId = rs.getString("id");

//                     ItemGeneric item = null;
//                     try {
//                         // Gọi Factory ở đây
//                         item = CreatorRegistry.getCreator(category).createItem(request);
//                     } catch (ItemValidationException e) {
//                         System.out.println("Lỗi validation khi load item: " + e.getMessage());
//                         // Bạn có thể xử lý thêm ở đây
//                     }

//                     if (item != null) {
//                         item.setId(itemId);
//                         // Bước 3: Load nốt các thuộc tính riêng từ bảng phụ
//                         item.loadSpecificDetails(con);
//                         try {
//                             item.validate(); 
//                         } catch (ItemValidationException e) {
//                             // Dữ liệu DB lỗi (ai đó đã sửa tay vào DB chẳng hạn)
//                             return null; 
//                         }
//                     }
//                     return item;
//                 }
//             }
//         } catch (SQLException e) {
//             e.printStackTrace();
//         }
//         return null;
//     }

//     public List<ItemGeneric> getAllItems(){
//           List<ItemGeneric> items = new ArrayList<>();
//           String sql = "SELECT * FROM ddd_items";

//           try (Connection con = new DatabaseConnection().getConnection();
//                PreparedStatement pst = con.prepareStatement(sql);
//                ResultSet rs = pst.executeQuery()){
//                       while (rs.next()){
//                          ItemRequest request = new ItemRequest(rs.getString("item_name"), rs.getString("description"), rs.getString("category"), rs.getString("seller_name"));
                         
//                          String category = rs.getString("category");
//                          String itemId = rs.getString("id");

//                          ItemGeneric item = null;

//                          try {
//                               item = CreatorRegistry.getCreator(category).createItem(request);
//                          } catch (ItemValidationException e){
//                                   System.out.println("Lỗi validation khi load itemL: " + e.getMessage());
//                          }

//                           if (item != null){
//                                item.setId(itemId);
//                                try {
//                                   item.loadSpecificDetails(con);
//                                   item.validate();
//                                   item.add(item);
//                                } catch (ItemValidationException e){
//                                         System.out.println("Dữ liệu item không hợp lệ: " + e.getMessage());
//                                }
//                           }
//                       }
//                }   catch (SQLException e){
//                     e.printStackTrace();
//                }      return items;
            
//     }

//    /* private ItemGeneric buildItemByCategory(String category) {
//         if (category == null) {
//             return null;
//         }

//         switch (category.toUpperCase()) {
//             case "ART":
//                 return new Art();
//             case "ELECTRONICS":
//                 return new Electronics();
//             case "VEHICLE":
//                 return new Vehicle();
//             default:
//                 return null;
//         }
//     }*/
// }