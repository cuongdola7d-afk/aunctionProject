package ddc.server.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class EnvConfig {
    private static final Logger LOGGER = Logger.getLogger(EnvConfig.class.getName());
    private static final Map<String, String> ENV_VALUES = loadEnv();

    private EnvConfig() {}

    public static String get(String envName, String propertyName) {
        return get(envName, propertyName, null);
    }

    public static String get(String envName, String propertyName, String defaultValue) {
        String value = System.getenv(envName);
        if (!isBlank(value)) {
            return value.trim();
        }

        value = System.getProperty(propertyName);
        if (!isBlank(value)) {
            return value.trim();
        }

        value = ENV_VALUES.get(envName);
        return isBlank(value) ? defaultValue : value.trim();
    }

    public static int getPort(String envName, String propertyName, int defaultValue) {
        String value = get(envName, propertyName);

        if (isBlank(value)) {
            return defaultValue;
        }

        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65535) {
                LOGGER.warning("Cong khong hop le: " + envName);
                return defaultValue;
            }
            return port;
        } catch (NumberFormatException e) {
            LOGGER.warning("Cong khong phai so: " + envName);
            return defaultValue;
        }
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
        } catch (IOException e) {
            LOGGER.warning("Khong doc duoc file .env: " + e.getMessage());
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

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
