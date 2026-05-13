package ddc.client.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientContext {
    public static final ExecutorService EXECUTOR = Executors.newVirtualThreadPerTaskExecutor();
    private static final Map<String, String> ENV_VALUES = loadEnv();

    public static final String SERVER_HOST = readConfig("DDC_SERVER_HOST", "ddc.server.host", "localhost");
    public static final int REQUEST_PORT = readRequestPort();
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
        if (value != null && !value.isBlank()) {
            return value;
        }

        value = ENV_VALUES.get(envName);
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

    private static int readRequestPort() {
        int legacyPort = readIntConfig("DDC_SERVER_PORT", "ddc.server.port", 8080);
        return readIntConfig("DDC_REQUEST_PORT", "ddc.request.port", legacyPort);
    }

    private static Map<String, String> loadEnv() {
        Map<String, String> values = new HashMap<>();
        Path envPath = findEnvPath();

        if (envPath == null) {
            return values;
        }

        try {
            for (String line : Files.readAllLines(envPath, StandardCharsets.UTF_8)) {
                parseLine(values, line);
            }
        } catch (IOException ignored) {
            return values;
        }

        return values;
    }

    private static Path findEnvPath() {
        Path currentPath = Path.of("").toAbsolutePath();

        while (currentPath != null) {
            Path envPath = currentPath.resolve(".env");
            if (Files.exists(envPath)) {
                return envPath;
            }
            currentPath = currentPath.getParent();
        }

        return null;
    }

    private static void parseLine(Map<String, String> values, String line) {
        String trimmed = line.trim();

        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }

        int equalIndex = trimmed.indexOf('=');
        if (equalIndex <= 0) {
            return;
        }

        String key = trimmed.substring(0, equalIndex).trim();
        String value = trimmed.substring(equalIndex + 1).trim();
        values.put(key, removeQuotes(value));
    }

    private static String removeQuotes(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }

        return value;
    }
}
