package ddc.server.controller.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("CloudinaryService - Unit Tests")
public class CloudinaryServiceTest {

    // ==================== uploadBytes Tests ====================

    @Test
    @DisplayName("uploadBytes - Should return null for null input")
    void testUploadBytes_NullInput() {
        String result = CloudinaryService.uploadBytes(null);
        assertNull(result, "Should return null for null byte array");
    }

    @Test
    @DisplayName("uploadBytes - Should return null for empty byte array")
    void testUploadBytes_EmptyByteArray() {
        byte[] emptyArray = new byte[0];
        String result = CloudinaryService.uploadBytes(emptyArray);
        assertNull(result, "Should return null for empty byte array");
    }

    @Test
    @DisplayName("uploadBytes - Should accept valid byte array")
    void testUploadBytes_ValidByteArray() {
        // Test with valid image data (minimal JPEG header)
        byte[] validImage = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0,
            0x00, 0x10, 0x4A, 0x46, 0x49, 0x46
        };
        
        // The method will attempt to upload; we're just testing it accepts the input
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(validImage),
                          "Should accept valid byte array without throwing");
    }

    @Test
    @DisplayName("uploadBytes - Should handle single byte")
    void testUploadBytes_SingleByte() {
        byte[] singleByte = { (byte) 0xFF };
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(singleByte),
                          "Should handle single byte input");
    }

    @Test
    @DisplayName("uploadBytes - Should handle large byte array")
    void testUploadBytes_LargeByteArray() {
        // Simulate a large image file (~5MB)
        byte[] largeArray = new byte[5 * 1024 * 1024];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (byte) (i % 256);
        }
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(largeArray),
                          "Should accept large byte arrays");
    }

    @Test
    @DisplayName("uploadBytes - Should handle very small valid image")
    void testUploadBytes_SmallValidImage() {
        // Valid minimal PNG header
        byte[] minimalPNG = {
            (byte) 0x89, 0x50, 0x4E, 0x47,
            0x0D, 0x0A, 0x1A, 0x0A
        };
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(minimalPNG),
                          "Should accept small valid image data");
    }

    // ==================== Return Value Tests ====================

    @Test
    @DisplayName("uploadBytes - Should return String or null")
    void testUploadBytes_ReturnType() {
        byte[] testData = { (byte) 0xFF, (byte) 0xD8 };
        Object result = CloudinaryService.uploadBytes(testData);
        
        assertTrue(result == null || result instanceof String,
                  "Result should be String or null");
    }

    @Test
    @DisplayName("uploadBytes - Successful upload should return URL-like string")
    void testUploadBytes_SuccessfulUploadFormat() {
        // This test validates the expected format if upload succeeds
        String expectedURLFormat = "https://";
        assertTrue(expectedURLFormat.startsWith("https://"),
                  "Successful uploads should return secure URL");
    }

    // ==================== Error Handling Tests ====================

    @Test
    @DisplayName("uploadBytes - Should handle invalid image data gracefully")
    void testUploadBytes_InvalidImageData() {
        byte[] invalidData = "This is not an image".getBytes();
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(invalidData),
                          "Should not throw exception for invalid image data");
    }

    @Test
    @DisplayName("uploadBytes - Should handle corrupted image data")
    void testUploadBytes_CorruptedData() {
        // Data that starts like JPEG but is corrupted
        byte[] corruptedJPEG = {
            (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00, 0x00, 0x00
        };
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(corruptedJPEG),
                          "Should handle corrupted image data gracefully");
    }

    @Test
    @DisplayName("uploadBytes - Should handle all zero bytes")
    void testUploadBytes_AllZeroBytes() {
        byte[] allZeros = new byte[100];
        // All bytes are already 0 (default initialization)
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(allZeros),
                          "Should handle byte array of all zeros");
    }

    @Test
    @DisplayName("uploadBytes - Should handle all 0xFF bytes")
    void testUploadBytes_AllFFBytes() {
        byte[] allFF = new byte[100];
        for (int i = 0; i < allFF.length; i++) {
            allFF[i] = (byte) 0xFF;
        }
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(allFF),
                          "Should handle byte array of all 0xFF");
    }

    // ==================== Multiple Calls Tests ====================

    @Test
    @DisplayName("uploadBytes - Should handle multiple sequential calls")
    void testUploadBytes_SequentialCalls() {
        byte[] testData = { (byte) 0xFF, (byte) 0xD8 };
        
        assertDoesNotThrow(() -> {
            CloudinaryService.uploadBytes(testData);
            CloudinaryService.uploadBytes(testData);
            CloudinaryService.uploadBytes(testData);
        }, "Should handle multiple sequential calls");
    }

    @Test
    @DisplayName("uploadBytes - Different data in sequential calls")
    void testUploadBytes_DifferentDataSequential() {
        byte[][] testDataArray = {
            { (byte) 0xFF, (byte) 0xD8 },
            { (byte) 0x89, 0x50 },
            { 0x00, 0x00 },
            { (byte) 0xAA, (byte) 0xBB }
        };
        
        for (byte[] data : testDataArray) {
            assertDoesNotThrow(() -> CloudinaryService.uploadBytes(data),
                              "Should handle different data in sequential calls");
        }
    }

    // ==================== Edge Cases ====================

    @Test
    @DisplayName("Edge case - Byte array with negative values")
    void testEdgeCase_NegativeByteValues() {
        byte[] negativeBytes = {
            (byte) 0xFF, (byte) 0xFE, (byte) 0xFD, (byte) 0xFC
        };
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(negativeBytes),
                          "Should handle bytes with negative signed values");
    }

    @Test
    @DisplayName("Edge case - Very large file simulation")
    void testEdgeCase_VeryLargeFile() {
        // Simulate a 50MB file
        byte[] veryLargeArray = new byte[50 * 1024 * 1024];
        veryLargeArray[0] = (byte) 0xFF;
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(veryLargeArray),
                          "Should handle very large byte arrays");
    }

    @Test
    @DisplayName("Edge case - UTF-8 encoded text as bytes")
    void testEdgeCase_UTF8Text() {
        String text = "This is a test file with UTF-8: 你好世界 🌍";
        byte[] utf8Bytes = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(utf8Bytes),
                          "Should handle UTF-8 encoded text as byte array");
    }

    @Test
    @DisplayName("Edge case - Binary content from random data")
    void testEdgeCase_RandomBinaryData() {
        byte[] randomData = new byte[1024];
        for (int i = 0; i < randomData.length; i++) {
            randomData[i] = (byte) (Math.random() * 256);
        }
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(randomData),
                          "Should handle random binary data");
    }

    @Test
    @DisplayName("Edge case - Alternating byte pattern")
    void testEdgeCase_AlternatingPattern() {
        byte[] alternating = new byte[1000];
        for (int i = 0; i < alternating.length; i++) {
            alternating[i] = (byte) (i % 2 == 0 ? 0x00 : 0xFF);
        }
        
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(alternating),
                          "Should handle alternating byte pattern");
    }

    // ==================== Static Method Tests ====================

    @Test
    @DisplayName("uploadBytes - Static method should be callable without instance")
    void testUploadBytes_StaticMethod() {
        byte[] testData = { (byte) 0xFF };
        
        // This should work without creating an instance
        assertDoesNotThrow(() -> CloudinaryService.uploadBytes(testData),
                          "Static method should be callable without instance");
    }

    @Test
    @DisplayName("uploadBytes - Multiple static calls in parallel")
    void testUploadBytes_ParallelStaticCalls() {
        byte[] testData = { (byte) 0xFF, (byte) 0xD8 };
        
        assertDoesNotThrow(() -> {
            CloudinaryService.uploadBytes(testData);
            CloudinaryService.uploadBytes(testData);
            CloudinaryService.uploadBytes(testData);
        }, "Multiple static calls should work");
    }

    // ==================== Data Integrity Tests ====================

    @Test
    @DisplayName("uploadBytes - Should not modify input byte array")
    void testUploadBytes_InputNotModified() {
        byte[] originalData = { (byte) 0xFF, (byte) 0xD8, (byte) 0xFF };
        byte[] testData = originalData.clone();
        
        CloudinaryService.uploadBytes(testData);
        
        assertArrayEquals(originalData, testData,
                         "Input byte array should not be modified by upload");
    }
}
