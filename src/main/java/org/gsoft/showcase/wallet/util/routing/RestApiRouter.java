package org.gsoft.showcase.wallet.util.routing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import org.apache.commons.io.IOUtils;
import org.gsoft.showcase.wallet.dto.ErrorVM;
import org.gsoft.showcase.wallet.error.ExceptionWithHttpCode;
import org.gsoft.showcase.wallet.error.InvalidJsonException;
import org.gsoft.showcase.wallet.error.MethodNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiRouter implements HttpHandler {

    private static final Logger log = LoggerFactory.getLogger(RestApiRouter.class);

    private final List<ApiRequestHandler> handlers;
    private final ObjectMapper objectMapper;

    RestApiRouter(List<ApiRequestHandler> handlers, ObjectMapper objectMapper) {
        this.handlers = handlers;
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();

            ApiRequestHandler requestHandler = handlers.stream()
                .filter(handler -> handler.getPathPattern().matcher(path).matches())
                .filter(handler -> handler.getHttpMethod().equalsIgnoreCase(exchange.getRequestMethod()))
                .findFirst()
                .orElseThrow(
                    () -> new MethodNotFoundException("method not found: " + exchange.getRequestMethod() + " " + path));

            Matcher pathMatcher = requestHandler.getPathPattern().matcher(path);

            if (!pathMatcher.matches()) {
                // should not happen, because we are selecting handler by matching
                throw new RuntimeException("path does not match pattern");
            }

            String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

            Object responseObject;

            if (!requestBody.isEmpty()) {
                Object requestObject;
                try {
                    requestObject = objectMapper.readValue(requestBody, requestHandler.getRequestType());
                } catch (Exception e) {
                    throw new InvalidJsonException(e.getMessage(), e);
                }
                responseObject = requestHandler.handleWithBody(pathMatcher, requestObject);
            } else {
                responseObject = requestHandler.handleWithoutBody(pathMatcher);
            }

            if (responseObject != null) {
                writeObjectResponse(200, responseObject, exchange);
            } else {
                exchange.sendResponseHeaders(200, 0);
            }
        } catch (Exception e) {
            handleException(e, exchange);
        } finally {
            exchange.close();
        }
    }

    private void handleException(Exception exception, HttpExchange exchange) throws IOException {
        log.error(
            "error while handling request: " + exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath(),
            exception);

        int httpCode = 500;
        String message = "internal server error";

        if (exception.getMessage() != null && !exception.getMessage().isEmpty()) {
            message = exception.getMessage();
        }

        if (exception instanceof ExceptionWithHttpCode) {
            httpCode = ((ExceptionWithHttpCode) exception).getHttpCode();
        }

        writeObjectResponse(httpCode, new ErrorVM(message), exchange);
    }

    private void writeObjectResponse(int httpCode, Object object, HttpExchange exchange)
        throws IOException {
        String responseBody = objectMapper.writeValueAsString(object);

        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(httpCode, responseBody.getBytes(StandardCharsets.UTF_8).length);

        IOUtils.write(responseBody, exchange.getResponseBody(), StandardCharsets.UTF_8);
    }
}
