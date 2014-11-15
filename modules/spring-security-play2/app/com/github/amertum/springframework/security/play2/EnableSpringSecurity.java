package com.github.amertum.springframework.security.play2;

import play.mvc.With;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Wraps the annotated action in a {@link SpringSecurityAction}.
 */
@With(SpringSecurityAction.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableSpringSecurity {

    /**
     * @return false (default) to redirect to configured page on {@link org.springframework.security.access.AccessDeniedException},
     * true to return {@link play.mvc.Controller#unauthorized()} directly (useful for ajax action request).
     */
    boolean unauthorizedOnAccessDenied() default false;

}
