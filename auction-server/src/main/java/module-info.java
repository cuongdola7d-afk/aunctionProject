module ddc.server {
    requires java.sql;
    requires com.google.gson;

    exports ddc.server;
    // exports ddc.server.config;
    // exports ddc.server.dao;
    // exports ddc.server.exception;
    // exports ddc.server.model.entity;
    // exports ddc.server.model.item;
    // exports ddc.server.model.transaction;
    // exports ddc.server.model.user;
    // exports ddc.server.network;
    // exports ddc.server.network.client;
    // exports ddc.server.network.message;
    // exports ddc.server.network.request;
    // exports ddc.server.network.response;
    // exports ddc.server.pattern.Singleton;
    // exports ddc.server.pattern.observer;
    // exports ddc.server.service;

    opens ddc.server.controller to com.google.gson;
    opens ddc.server.controller.handler to com.google.gson;
    opens ddc.server.controller.service to com.google.gson, org.junit.platform.commons; // Bắt buộc để gọi được AuctionService, Cho phép JUnit can thiệp sâu để chạy Test

    opens ddc.server.model.entity to com.google.gson;
    opens ddc.server.model.transaction to com.google.gson; // Bắt buộc để gọi được AuctionStatus
    opens ddc.server.model.item to com.google.gson;
    opens ddc.server.model.user to com.google.gson;

    opens ddc.server.network.response to com.google.gson;

    opens ddc.server.pattern.factory to com.google.gson, org.junit.platform.commons;

    opens ddc.server.exception to com.google.gson;
    opens ddc.server.pattern.Singleton to org.junit.platform.commons;
    
}