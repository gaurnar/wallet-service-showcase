package org.gsoft.showcase.wallet.util.routing;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestApiRoutingBuilder {

    public interface RequestHandlerAction<T> {
        T handle(Matcher pathMatcher);
    }

    public interface RequestWithBodyHandlerAction<T, V> {
        V handle(Matcher pathMatcher, T body);
    }

    private final List<ApiRequestHandler> handlers = new ArrayList<>();
    private final ObjectMapper objectMapper;

    public RestApiRoutingBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> RestApiRoutingBuilder get(Pattern pathRegex, RequestHandlerAction<T> action) {
        handlers.add(new BaseApiRequestWithoutBodyHandler<T>("GET", pathRegex) {
            @Override
            public T handleWithoutBody(Matcher pathMatcher) {
                return action.handle(pathMatcher);
            }
        });
        return this;
    }

    public <T> RestApiRoutingBuilder get(String path, RequestHandlerAction<T> action) {
        return get(Pattern.compile(Pattern.quote(path)), action);
    }

    public <T, V> RestApiRoutingBuilder post(Pattern pathRegex, Class<T> requestType,
                                              RequestWithBodyHandlerAction<T, V> action) {
        handlers.add(new BaseApiRequestWithBodyHandler<T, V>("POST", pathRegex, requestType) {
            @Override
            public V handleWithBody(Matcher pathMatcher, T body) {
                return action.handle(pathMatcher, body);
            }
        });
        return this;
    }

    public <T, V> RestApiRoutingBuilder post(String path, Class<T> requestType,
                                             RequestWithBodyHandlerAction<T, V> action) {
        return post(Pattern.compile(Pattern.quote(path)), requestType, action);
    }

    public RestApiRouter build() {
        return new RestApiRouter(new ArrayList<>(handlers), objectMapper);
    }
}
