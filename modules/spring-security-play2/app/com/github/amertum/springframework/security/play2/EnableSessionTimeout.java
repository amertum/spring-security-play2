package com.github.amertum.springframework.security.play2;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wraps the annotated action in a {@link SessionTimeoutAction}.
 * TODO merge with @EnableSpringSecurity
 */
@With(SessionTimeoutAction.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSessionTimeout {

    /**
     * @return true (default) to redirect to configured page on timeout,
     * false to redirect to current request uri which is useful for login page.
     */
    boolean redirectToConfiguredPageOnTimeout() default true;

    /**
     * @return true to only update session date which will not check and make session timeout (which is useful for login
     * page, the update will prevent timeout on next page), false else (default).
     */
    boolean updateOnly() default false;

}
