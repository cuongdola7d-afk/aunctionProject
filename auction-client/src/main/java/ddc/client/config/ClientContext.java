package ddc.client.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientContext {
    public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();

    public static final String SERVER_HOST = readConfig("DDC_SERVER_HOST", "ddc.server.host", "localhost");
    public static final int REQUEST_PORT = readIntConfig("DDC_SERVER_PORT", "ddc.server.port", 8080);
    public static final int REALTIME_PORT = readIntConfig("DDC_REALTIME_PORT", "ddc.realtime.port", 5555);
    public static final int IMAGE_PORT = readIntConfig("DDC_IMAGE_PORT", "ddc.image.port", 8081);

    private ClientContext() {
    }

    private static String readConfig(String envName, String propertyName, String defaultValue) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) {
            return value;
        }

        value = System.getProperty(propertyName);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static int readIntConfig(String envName, String propertyName, int defaultValue) {
        String value = readConfig(envName, propertyName, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
