package org.gsoft.showcase.wallet.util.routing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

interface ApiRequestHandler<T, V> {
    Pattern getPathPattern();

    String getHttpMethod();

    Class<T> getRequestType();

    V handleWithBody(Matcher pathMatcher, T body);

    V handleWithoutBody(Matcher pathMatcher);
}
