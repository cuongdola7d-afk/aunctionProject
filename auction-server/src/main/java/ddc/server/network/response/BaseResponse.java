package ddc.server.network.response;

public class BaseResponse extends Response<BaseResponse> {
    
    private String message; // Thêm biến để chứa chuỗi JSON hoặc thông báo lỗi

    public String getMessage() {
        return message;
    }

    // Tạo hàm setMessage chuẩn theo dạng Builder Pattern (giống setStatus)
    public BaseResponse setMessage(String message) {
        this.message = message;
        return this; // Trả về chính nó để có thể nối .setStatus().setMessage()
    }
}