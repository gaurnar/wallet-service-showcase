package org.gsoft.showcase.wallet.util.routing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import org.apache.commons.io.IOUtils;

public class RestApiRouter implements HttpHandler {

    private final List<ApiRequestHandler> handlers;
    private final ObjectMapper objectMapper;

    RestApiRouter(List<ApiRequestHandler> handlers, ObjectMapper objectMapper) {
        this.handlers = handlers;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath();

        ApiRequestHandler requestHandler = handlers.stream()
            .filter(handler -> handler.getPathPattern().matcher(path).matches())
            .filter(handler -> handler.getHttpMethod().equalsIgnoreCase(exchange.getRequestMethod()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("no handlers for: " + exchange.getRequestMethod() + " " + path));

        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");

        Matcher pathMatcher = requestHandler.getPathPattern().matcher(path);

        String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

        Object responseObject;

        if (!requestBody.isEmpty()) {
            Object requestObject = objectMapper.readValue(requestBody, requestHandler.getRequestType());
            responseObject = requestHandler.handleWithBody(pathMatcher, requestObject);
        } else {
            responseObject = requestHandler.handleWithoutBody(pathMatcher);
        }

        if (responseObject != null) {
            String responseBody = objectMapper.writeValueAsString(responseObject);
            exchange.sendResponseHeaders(200, responseBody.getBytes(StandardCharsets.UTF_8).length);
            IOUtils.write(responseBody, exchange.getResponseBody(), StandardCharsets.UTF_8);
        } else {
            exchange.sendResponseHeaders(200, 0);
        }

        exchange.close();
    }
}
