module ddc.server {
    requires java.sql;
    requires java.logging;
    requires com.google.gson;
    requires com.zaxxer.hikari;
    requires cloudinary.http44;
    requires cloudinary.core;

    exports ddc.server;

    opens ddc.server.controller to com.google.gson;
    opens ddc.server.controller.handler to com.google.gson;
    opens ddc.server.controller.service to com.google.gson, org.junit.platform.commons;

    opens ddc.server.model.entity to com.google.gson;
    opens ddc.server.model.transaction to com.google.gson;
    opens ddc.server.model.item to com.google.gson;
    opens ddc.server.model.user to com.google.gson;

    opens ddc.server.network.response to com.google.gson;

    opens ddc.server.network to com.google.gson, cloudinary.core;
    opens ddc.server.network.request to com.google.gson;
    opens ddc.server.network.message to com.google.gson;
    opens ddc.server.network.client to com.google.gson;

    opens ddc.server.pattern.factory to com.google.gson, org.junit.platform.commons;

    opens ddc.server.exception to com.google.gson;
    opens ddc.server.pattern.Singleton to org.junit.platform.commons;

    opens ddc.server.config to com.zaxxer.hikari;
}
