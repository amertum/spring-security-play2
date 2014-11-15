package com.github.amertum.springframework.security.play2;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.F;
import play.mvc.*;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.github.amertum.springframework.security.play2.context.HttpSessionSecurityContextRepository.getSecuritySessionUsername;

/**
 * Provide {@link Http.Session} timeout as action composition.
 * <p>
 * Prefers use of {@link EnableSessionTimeout} annotation instead of {@code With(SessionTimeoutAction)} as it may throw
 * a ClassCastException about Proxy when used with spring-aop.
 *
 * TODO move this feature to SpringSecurityAction to prevent timeout notification before security login
 */
public class SessionTimeoutAction
        extends Action<EnableSessionTimeout> {

    public SessionTimeoutAction(
            final Clock clock,
            final int securitySessionDurationMinutes,
            final Call redirectRoute
    ) {
        this.clock = clock;
        this.securitySessionDurationMinutes = securitySessionDurationMinutes;
        this.redirectRoute = redirectRoute;
    }

    public F.Promise<Result> call(Http.Context httpContext) throws Throwable {
        final F.Promise<Result> resultPromise;

        if (!this.configuration.updateOnly() && checkSessionTimeout(httpContext)) {
            forceSessionTimeout();

            Controller.flash(SESSION_TIMEOUT_FLASH_KEY, "true");

            logger.debug("session expired, redirect (to configured page: {})...", this.configuration.redirectToConfiguredPageOnTimeout());
            final Result result = this.configuration.redirectToConfiguredPageOnTimeout()
                    ? Results.redirect(SpringSecurityAction.routeWithRequestContinueUrl(redirectRoute, httpContext.request()))
                    : redirect(httpContext.request().uri());
            resultPromise = F.Promise.pure(result); // TODO review play2 promise use
        }
        else {
            resultPromise = delegate.call(httpContext);
        }

        updateSessionDate(httpContext);

        return resultPromise;
    }

    private boolean checkSessionTimeout(
            final Http.Context context
    ) {
        final String lastRequestDate = context.session().get(SESSION_TIMEOUT_TIMESTAMP_PARAM);
        final Instant lastRequestInstant = Instant.ofEpochMilli(NumberUtils.toLong(lastRequestDate));
        final Instant instant = clock.instant();

        final boolean expired = lastRequestInstant.plus(securitySessionDurationMinutes, ChronoUnit.MINUTES).isBefore(instant);
        logger.debug("session of user '{}' expired: {}", getSecuritySessionUsername(context), expired);

        return expired;
    }

    private void updateSessionDate(
            final Http.Context context
    ) {
        final Instant instant = clock.instant();
        context.session().put(SESSION_TIMEOUT_TIMESTAMP_PARAM, Long.toString(instant.toEpochMilli()));
    }

    public static void forceSessionTimeout() {
        Http.Context.current().session().clear();
    }

    public static boolean isSessionTimeout() {
        return Controller.flash(SESSION_TIMEOUT_FLASH_KEY) != null;
    }

    private static final String SESSION_TIMEOUT_TIMESTAMP_PARAM = "session-timeout-timestamp";
    private static final String SESSION_TIMEOUT_FLASH_KEY = "session-timeout-expired";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Clock clock;
    private final int securitySessionDurationMinutes;
    private final Call redirectRoute;

}
