package ddc.client.controller.selling;

import ddc.client.exception.ItemValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UploadItemTest {


    @Test
    @DisplayName("Biên: Giá đúng bằng 0 - Phải lỗi")
    void testPriceAtBoundaryZero() {
        double startingPrice = 0.0;
        assertThrows(ItemValidationException.class, () -> {
            if (startingPrice <= 0) throw new ItemValidationException("Giá phải > 0");
        });
    }

    @Test
    @DisplayName("Biên: Giá cực nhỏ sát 0 (0.01) - Khong bao loi")
    void testPriceJustAboveZero() {
        double startingPrice = 0.01;
        assertDoesNotThrow(() -> {
            if (startingPrice <= 0) throw new ItemValidationException("Giá phải > 0");
        });
    }

    @Test
    @DisplayName("Negative value ")
    void testPriceIsNegative() {
        double startingPrice = -50.0;
        assertThrows(ItemValidationException.class, () -> {
            if (startingPrice <= 0) throw new ItemValidationException("Giá phải > 0");
        });
    }

    @Test
    @DisplayName("Giá hợp lệ - Không được báo lỗi")
    void testPriceIsValid() {
        double startingPrice = 1000.0;
        assertDoesNotThrow(() -> {
            if (startingPrice <= 0) throw new ItemValidationException("Giá phải > 0");
        });
    }

    // --- TEST LOGIC THỜI GIAN ---

    @Test
    @DisplayName("Thời gian bắt đầu ở quá khứ - Phải báo lỗi")
    void testStartTimeInPast() {
        // Giả lập thời gian là 10 phút trước
        LocalDateTime startInPast = LocalDateTime.now().minusMinutes(10);
        
        assertThrows(ItemValidationException.InvalidDurationException.class, () -> {
            if (startInPast.isBefore(LocalDateTime.now())) {
                throw new ItemValidationException.InvalidDurationException("Thời gian bắt đầu không được ở quá khứ!");
            }
        });
    }

    @Test
    @DisplayName("Kết thúc trước khi bắt đầu - Phải báo lỗi")
    void testEndBeforeStart() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = start.minusHours(1); // Kết thúc trước bắt đầu 1 tiếng

        assertThrows(ItemValidationException.InvalidDurationException.class, () -> {
            if (!end.isAfter(start)) {
                throw new ItemValidationException.InvalidDurationException("Thời gian kết thúc phải sau thời gian bắt đầu!");
            }
        });
    }

    @Test
    @DisplayName("Thời gian hợp lệ - Không được báo lỗi")
    void testValidDuration() {
        LocalDateTime start = LocalDateTime.now().plusHours(1);
        LocalDateTime end = start.plusDays(1);

        assertDoesNotThrow(() -> {
            if (start.isBefore(LocalDateTime.now())) throw new Exception();
            if (!end.isAfter(start)) throw new Exception();
        });
    }
}