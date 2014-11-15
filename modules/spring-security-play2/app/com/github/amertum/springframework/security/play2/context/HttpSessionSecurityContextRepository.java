package com.github.amertum.springframework.security.play2.context;

import com.github.amertum.springframework.security.util.Roles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionOperations;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import play.mvc.Controller;
import play.mvc.Http;

import java.util.function.Supplier;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

/**
 * A {@code SecurityContextRepository} implementation which stores the security context in the {@code Http.Session}
 * between requests.
 * <p>
 * Must be called first in http request filter or action composition.
 * <p>
 * Inspired by {@code org.springframework.security.web.context.HttpSessionSecurityContextRepository}.
 */
public class HttpSessionSecurityContextRepository
        implements SecurityContextRepository {

    public HttpSessionSecurityContextRepository(
            final UserDetailsService userService,
            final Supplier<SecurityExpressionOperations> securityExpressionOperationsSupplier
    ) {
        this.userService = userService;
        this.securityExpressionOperationsSupplier = securityExpressionOperationsSupplier;
    }

    @Override
    public SecurityContext loadContext(Http.Context context) {
        final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(securityContext);

        UserDetails user;
        if (isHttpContextSessionContainsSecurityUsername(context)) {
            final String username = getSecuritySessionUsername(context);
            logger.debug("Security username is {}", username);

            try {
                final boolean anonymous = SECURITY_ANONYMOUS_USERNAME.equals(username);
                user = anonymous
                        ? null
                        : userService.loadUserByUsername(username);
                logger.debug("Security loaded user is {}", anonymous ? SECURITY_ANONYMOUS_USERNAME : user.getUsername());
            }
            catch (UsernameNotFoundException e) {
                logger.warn("HttpContext.session does not contain Security username '{}'", username);
                user = null;
            }
        }
        else {
            logger.debug("HttpContext.session does not contain Security username");
            user = null;
        }

        final Authentication authentication = (user == null)
                ? createAnonymousAuthenticationToken()
                : new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        logger.debug("Authentication is {}", authentication.getClass().getSimpleName());

        securityContext.setAuthentication(authentication);
        context.request().setUsername(authentication.getName());
        context.args.put(SECURITY_EXPRESSION_OPERATIONS_KEY, securityExpressionOperationsSupplier.get());

        return securityContext;
    }

    static AnonymousAuthenticationToken createAnonymousAuthenticationToken() {
        return new AnonymousAuthenticationToken(SECURITY_ANONYMOUS_KEY, SECURITY_ANONYMOUS_USERNAME, createAuthorityList(Roles.ROLE_ANONYMOUS));
    }

    @Override
    public void saveContext(SecurityContext securityContext, Http.Context context) {
        final Authentication authentication = securityContext.getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            logger.debug("remove SecurityContext.session param");
            context.session().remove(SECURITY_USERNAME_PARAM);
        }
        else {
            final String username = authentication.getName();
            logger.debug("set SecurityContext.session username: {}", username);
            context.session().put(SECURITY_USERNAME_PARAM, username);
        }
    }

    @Override
    public boolean containsContext(Http.Context context) {
        return isHttpContextSessionContainsSecurityUsername(context);
    }

    public static boolean isAuthenticationFailure() {
        return Controller.flash(SECURITY_AUTHENTICATION_FAILURE_FLASH_KEY) != null;
    }

    public static void setAuthenticationFailure() {
        Controller.flash(SECURITY_AUTHENTICATION_FAILURE_FLASH_KEY, "true");
    }

    public static SecurityExpressionOperations getSecurityExpressionOperations(Http.Context httpContext) {
        return (SecurityExpressionOperations) httpContext.args.get(SECURITY_EXPRESSION_OPERATIONS_KEY);
    }

    public static String getSecuritySessionUsername(Http.Context context) {
        return context.session().get(SECURITY_USERNAME_PARAM);
    }

    private static boolean isHttpContextSessionContainsSecurityUsername(Http.Context context) {
        return context.session().containsKey(SECURITY_USERNAME_PARAM);
    }

    private static final String SECURITY_EXPRESSION_OPERATIONS_KEY = "SECURITY_EXPRESSION_OPERATIONS";
    public static final String SECURITY_USERNAME_PARAM = "security-username";
    public static final String SECURITY_ANONYMOUS_KEY = "security-anonymous-key";
    public static final String SECURITY_AUTHENTICATION_FAILURE_FLASH_KEY = "security-auth-failure";
    public static final String SECURITY_ANONYMOUS_USERNAME = "ANONYMOUS";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserDetailsService userService;
    private final Supplier<SecurityExpressionOperations> securityExpressionOperationsSupplier;

}
