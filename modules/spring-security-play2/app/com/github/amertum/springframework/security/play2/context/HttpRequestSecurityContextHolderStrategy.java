package com.github.amertum.springframework.security.play2.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.core.context.SecurityContextImpl;
import play.Logger;
import play.Play;
import play.mvc.Http;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * A spring {@code SecurityContextHolderStrategy} for <em>Playframework 2.x</em> which store the {@link SecurityContext}
 * in current {@link play.mvc.Http.Context}.
 * <p>
 * Call {@link #setSecurityStrategy()} to register this {@link SecurityContextHolderStrategy} on application start.
 */
public class HttpRequestSecurityContextHolderStrategy
        implements SecurityContextHolderStrategy {

    public static void setSecurityStrategy() {
        final String strategyName = HttpRequestSecurityContextHolderStrategy.class.getName();
        if (Play.isProd()) {
            Logger.info("(PROD mode) SecurityContextHolder.setStrategyName('" + strategyName + "')");
            SecurityContextHolder.setStrategyName(strategyName);
        }
        else {
            // we use reflection hack as the ClassLoader in dev mode is not managed the same way as in prod mode
            Logger.info("(DEV mode) SecurityContextHolder.strategy = new " + strategyName + "() // using reflection hack");
            try {
                final Field strategy = SecurityContextHolder.class.getDeclaredField("strategy");
                strategy.setAccessible(true);
                strategy.set(null, new HttpRequestSecurityContextHolderStrategy());
            }
            catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    @Override
    public void clearContext() {
        Http.Context.current().args.remove(SECURITY_CONTEXT_PARAM);
    }

    @Override
    public SecurityContext getContext() {
        final boolean containsSecurityContext = (Http.Context.current.get() != null) && Http.Context.current().args.containsKey(SECURITY_CONTEXT_PARAM);
        if (!containsSecurityContext) {
            return SecurityContextHolder.createEmptyContext();
        }

        return (SecurityContext) Http.Context.current().args.get(SECURITY_CONTEXT_PARAM);
    }

    @Override
    public void setContext(SecurityContext context) {
        Http.Context.current().args.put(SECURITY_CONTEXT_PARAM, Objects.requireNonNull(context));
    }

    @Override
    public SecurityContext createEmptyContext() {
        return new SecurityContextImpl();
    }

    private static final String SECURITY_CONTEXT_PARAM = "SECURITY_CONTEXT";

}
