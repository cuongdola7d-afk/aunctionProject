package ddc.server.controller.service;

import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryService {
    private static final Cloudinary cloudinary;

    static {
        // Khởi tạo với thông số từ Dashboard Cloudinary của bạn
        cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", "dl3xzn3nf",
            "api_key", "547674336556818",
            "api_secret", "RMNUc5newpHvesEoWBJmf_aK_FU",
            "secure", true
        ));
    }

    /**
     * Nhận mảng byte từ RequestMessage và đẩy lên mây
     * @param imageData mảng byte lấy từ request.getImageData()
     * @return URL ảnh dạng https://... hoặc null nếu lỗi
     */
    public static String uploadBytes(byte[] imageData) {
        if (imageData == null || imageData.length == 0) return null;
        try {
            // Cloudinary hỗ trợ truyền trực tiếp mảng byte vào hàm upload
            Map<?, ?> uploadResult = cloudinary.uploader().upload(imageData, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            System.err.println(">>> LOI UPLOAD CLOUDINARY: " + e.getMessage());
            return null;
        }
    }
}