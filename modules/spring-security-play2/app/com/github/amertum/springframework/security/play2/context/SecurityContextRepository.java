package com.github.amertum.springframework.security.play2.context;

import org.springframework.security.core.context.SecurityContext;
import play.mvc.Http;

/**
 * Strategy used for persisting a {@link SecurityContext} between requests.
 * <p>
 * The persistence mechanism used will depend on the implementation, but most commonly the <tt>Http.Context</tt> will
 * be used to store the context.
 * <p>
 * Inspired by {@code org.springframework.security.web.context.SecurityContextRepository}.
 */
public interface SecurityContextRepository {

    /**
     * Obtains the security context for the supplied request. For an unauthenticated user, an empty context
     * implementation should be returned. This method should not return null.
     *
     * @param httpContext the current httpContext which the context should be loaded from.
     *
     * @return The security context which should be used for the current request, never null.
     */
    SecurityContext loadContext(Http.Context httpContext);

    /**
     * Stores the security context on completion of a request.
     *
     * @param securityContext the non-null context which was obtained from the holder.
     * @param httpContext the current httpContext which the context should be saved to.
     */
    void saveContext(SecurityContext securityContext, Http.Context httpContext);

    /**
     * Allows the repository to be queried as to whether it contains a security context for the
     * current request.
     *
     * @param httpContext the current httpContext
     * @return true if a context is found for the httpContext, false otherwise
     */
    boolean containsContext(Http.Context httpContext);

}
