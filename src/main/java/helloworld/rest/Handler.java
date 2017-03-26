package helloworld.rest;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class Handler implements HttpHandler {

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getResponseSender().send("Hello World!");
        return;
    }
}