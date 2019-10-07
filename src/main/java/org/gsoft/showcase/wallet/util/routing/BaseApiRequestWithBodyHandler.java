package org.gsoft.showcase.wallet.util.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class BaseApiRequestWithBodyHandler<T, V> implements ApiRequestHandler<T, V> {

    private final String httpMethod;
    private final Pattern pathPattern;
    private final Class<T> requestType;

    BaseApiRequestWithBodyHandler(String httpMethod, Pattern pathPattern, Class<T> requestType) {
        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
        this.requestType = requestType;
    }

    @Override
    public abstract V handleWithBody(Matcher pathMatcher, T body);

    @Override
    public Pattern getPathPattern() {
        return pathPattern;
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public Class<T> getRequestType() {
        return requestType;
    }

    @Override
    public V handleWithoutBody(Matcher pathMatcher) {
        return handleWithBody(pathMatcher, null);
    }
}
