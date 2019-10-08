package org.gsoft.showcase.wallet.util.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.gsoft.showcase.wallet.error.RequestBodyIsNotExpectedException;

abstract class BaseApiRequestWithoutBodyHandler<T> implements ApiRequestHandler<Void, T> {

    private final String httpMethod;
    private final Pattern pathPattern;

    BaseApiRequestWithoutBodyHandler(String httpMethod, Pattern pathPattern) {
        this.httpMethod = httpMethod;
        this.pathPattern = pathPattern;
    }

    @Override
    public abstract T handleWithoutBody(Matcher pathMatcher);

    @Override
    public Pattern getPathPattern() {
        return pathPattern;
    }

    @Override
    public String getHttpMethod() {
        return httpMethod;
    }

    @Override
    public Class<Void> getRequestType() {
        throw new RequestBodyIsNotExpectedException();
    }

    @Override
    public T handleWithBody(Matcher pathMatcher, Void body) {
        throw new RequestBodyIsNotExpectedException();
    }
}
