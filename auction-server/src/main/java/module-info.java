module ddc.server {
    requires java.sql;
    requires com.google.gson;

    exports ddc.server;

    // opens gemini.server.model.entity to com.google.gson;
    // opens gemini.server.model.user to com.google.gson;
    // opens gemini.server.model.item to com.google.gson;
    // opens gemini.server.model.transaction to com.google.gson;
}