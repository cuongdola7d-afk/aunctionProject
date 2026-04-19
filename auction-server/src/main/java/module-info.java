module ddc.server {
    requires java.sql;
    requires com.google.gson;

    exports ddc.server;
    exports ddc.server.controller;
    exports ddc.server.exception;
    exports ddc.server.model.entity;
    exports ddc.server.model.item;
    exports ddc.server.model.transaction;
    exports ddc.server.model.user;
    exports ddc.server.network.client;
    exports ddc.server.pattern.observer;
    // exports ddc.server.service;


    //exports ddc.server.network.dispatcher;
    exports ddc.server.network.message;
    exports ddc.server.network.request;
    exports ddc.server.network.response;

    opens ddc.server.model.entity to com.google.gson;
    opens ddc.server.model.item to com.google.gson;
    opens ddc.server.model.transaction to com.google.gson;
    opens ddc.server.model.user to com.google.gson;
    opens ddc.server.network.client to com.google.gson;
    opens ddc.server.pattern.observer to com.google.gson;
}