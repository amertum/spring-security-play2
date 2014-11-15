package com.github.amertum.springframework.security.play2;

import com.github.amertum.springframework.security.play2.context.HttpSessionSecurityContextRepository;
import com.github.amertum.springframework.security.util.Urls;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import play.libs.F;
import play.mvc.Action;
import play.mvc.Call;
import play.mvc.Http;
import play.mvc.Result;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static java.util.Optional.ofNullable;

/**
 * Provide {@link com.github.amertum.springframework.security.play2.context.SecurityContextRepository} loading and saving during http
 * request.
 * <p>
 * Prefers use of {@link EnableSpringSecurity} annotation instead of {@code With(EnableSpringSecurity)} as it may throw
 * a ClassCastException about Proxy when used with spring-aop.
 */
public class SpringSecurityAction
        extends Action<EnableSpringSecurity> {

    public SpringSecurityAction(
            final HttpSessionSecurityContextRepository securityContextRepository,
            final Call redirectRoute
    ) {
        this.securityContextRepository = securityContextRepository;
        this.redirectRoute = redirectRoute;
    }

    public F.Promise<Result> call(Http.Context httpContext) throws Throwable {
        try {
            logger.debug("SecurityContextRepository.loadContext...");
            securityContextRepository.loadContext(httpContext);

            final F.Promise<Result> resultPromise = delegate.call(httpContext);

            logger.debug("SecurityContextRepository.saveContext...");
            securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpContext);

            return resultPromise;
        }
        catch (AccessDeniedException e) {
            final boolean anonymous = authenticationTrustResolver.isAnonymous(SecurityContextHolder.getContext().getAuthentication());
            final boolean redirect = (anonymous && !this.configuration.unauthorizedOnAccessDenied());

            final Result result = redirect
                    ? redirect(routeWithRequestContinueUrl(this.redirectRoute, httpContext.request()))
                    :  unauthorized("UNAUTHORIZED"); // TODO provide custom result ?
            return F.Promise.pure(result);
        }
    }

    public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver) {
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    public static Call requestContinueUrlOrDefaultRoute(Http.Request request, Call defaultRoute) {
        final String targetUrl = retrieveContinueUrl(request, false);

        final Call route = targetUrl.isEmpty()
                ? defaultRoute
                : new play.api.mvc.Call("GET", targetUrl);
        return route;
    }

    public static String routeWithRequestContinueUrl(Call route, Http.Request request) {
        String continueUrl = retrieveContinueUrl(request, false);

        final String url = continueUrl.isEmpty()
                ? encodeUtf8(request.uri())
                : continueUrl;
        return buildRedirectUrl(route.url(), url);
    }

    private static String retrieveContinueUrl(Http.Request request, boolean encode) {
        final String targetUrl = ofNullable(request.getQueryString(CONTINUE_URL_PARAM))
                .filter(t -> !t.isEmpty())
                .map(Urls::extractUrlCompleteFilePart)
                .orElse("");

        return encode
                ? encodeUtf8(targetUrl)
                : targetUrl;
    }

    private static String buildRedirectUrl(String url, String toUrl) {
        return url + "?" + CONTINUE_URL_PARAM + "=" + toUrl;
    }

    private static String encodeUtf8(final String content) {
        try {
            return URLEncoder.encode(content, "UTF-8");
        } catch (UnsupportedEncodingException problem) {
            throw new IllegalArgumentException(problem);
        }
    }

    private static final String CONTINUE_URL_PARAM = "continue";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HttpSessionSecurityContextRepository securityContextRepository;
    private final Call redirectRoute;

    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

}
